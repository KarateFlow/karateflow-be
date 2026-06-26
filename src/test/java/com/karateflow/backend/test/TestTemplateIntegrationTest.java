package com.karateflow.backend.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.karateflow.backend.BaseIntegrationTest;
import com.karateflow.backend.test.domain.model.MeasurementUnit;
import com.karateflow.backend.test.dto.request.CreateTestTemplateRequest;
import com.karateflow.backend.test.dto.request.TemplateExerciseRequest;
import com.karateflow.backend.test.dto.request.UpdateTestTemplateRequest;
import com.karateflow.backend.test.persistence.repository.TestTemplateMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestTemplateIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestTemplateMongoRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldPerformCrudOnTestTemplates() throws Exception {
        // 1. Create a new test template
        final CreateTestTemplateRequest createRequest = CreateTestTemplateRequest.builder()
                .name("Speed Endurance Test")
                .description("Measures speed and endurance")
                .exercises(List.of(
                        TemplateExerciseRequest.builder()
                                .exerciseTitle("Shuttle Run")
                                .unit(MeasurementUnit.SEC)
                                .greaterIsBetter(false)
                                .build()
                ))
                .build();

        final String createResponseJson = mockMvc.perform(post("/api/v1/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Speed Endurance Test"))
                .andExpect(jsonPath("$.description").value("Measures speed and endurance"))
                .andExpect(jsonPath("$.exercises[0].exerciseTitle").value("Shuttle Run"))
                .andReturn().getResponse().getContentAsString();

        final String templateId = objectMapper.readTree(createResponseJson).get("id").asText();

        // Verify it exists in Mongo
        assertThat(repository.findById(templateId)).isPresent();

        // 2. Retrieve all templates
        mockMvc.perform(get("/api/v1/templates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(templateId));

        // 3. Retrieve template by ID
        mockMvc.perform(get("/api/v1/templates/{id}", templateId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(templateId))
                .andExpect(jsonPath("$.name").value("Speed Endurance Test"));

        // 4. Update the template
        final UpdateTestTemplateRequest updateRequest = UpdateTestTemplateRequest.builder()
                .name("Updated Speed Endurance Test")
                .description("Updated description")
                .exercises(List.of(
                        TemplateExerciseRequest.builder()
                                .exerciseTitle("Sprint 100m")
                                .unit(MeasurementUnit.SEC)
                                .greaterIsBetter(false)
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/v1/templates/{id}", templateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Speed Endurance Test"))
                .andExpect(jsonPath("$.exercises[0].exerciseTitle").value("Sprint 100m"));

        // 5. Delete the template
        mockMvc.perform(delete("/api/v1/templates/{id}", templateId))
                .andExpect(status().isNoContent());

        // Verify it was deleted from Mongo
        assertThat(repository.findById(templateId)).isEmpty();

        // Verify 404 on get
        mockMvc.perform(get("/api/v1/templates/{id}", templateId))
                .andExpect(status().isNotFound());
    }
}
