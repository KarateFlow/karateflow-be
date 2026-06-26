package com.karateflow.backend.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.karateflow.backend.common.exception.TestTemplateNotFoundException;
import com.karateflow.backend.common.handler.GlobalExceptionHandler;
import com.karateflow.backend.test.domain.model.MeasurementUnit;
import com.karateflow.backend.test.dto.request.CreateTestTemplateRequest;
import com.karateflow.backend.test.dto.request.TemplateExerciseRequest;
import com.karateflow.backend.test.dto.request.UpdateTestTemplateRequest;
import com.karateflow.backend.test.dto.response.TestTemplateResponse;
import com.karateflow.backend.test.usecase.CreateTestTemplateUseCase;
import com.karateflow.backend.test.usecase.DeleteTestTemplateUseCase;
import com.karateflow.backend.test.usecase.RetrieveTestTemplateUseCase;
import com.karateflow.backend.test.usecase.RetrieveTestTemplatesUseCase;
import com.karateflow.backend.test.usecase.UpdateTestTemplateUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestTemplateController.class)
@Import(GlobalExceptionHandler.class)
class TestTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private CreateTestTemplateUseCase createUseCase;

    @MockitoBean
    private RetrieveTestTemplatesUseCase retrieveAll;

    @MockitoBean
    private RetrieveTestTemplateUseCase retrieveOne;

    @MockitoBean
    private UpdateTestTemplateUseCase updateUseCase;

    @MockitoBean
    private DeleteTestTemplateUseCase deleteUseCase;

    @Test
    void shouldCreateTemplateSuccessfully() throws Exception {
        final CreateTestTemplateRequest request = CreateTestTemplateRequest.builder()
                .name("General Test")
                .description("Desc")
                .exercises(List.of(
                        TemplateExerciseRequest.builder()
                                .exerciseTitle("Pushups")
                                .unit(MeasurementUnit.COUNT)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();

        final TestTemplateResponse response = TestTemplateResponse.builder()
                .id("template-1")
                .name("General Test")
                .build();

        when(createUseCase.execute(any(CreateTestTemplateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("template-1"))
                .andExpect(jsonPath("$.name").value("General Test"));
    }

    @Test
    void shouldRetrieveAllTemplatesSuccessfully() throws Exception {
        final List<TestTemplateResponse> response = List.of(
                TestTemplateResponse.builder().id("template-1").name("General Test").build()
        );

        when(retrieveAll.execute()).thenReturn(response);

        mockMvc.perform(get("/api/v1/templates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("template-1"));
    }

    @Test
    void shouldGetTemplateDetailsSuccessfully() throws Exception {
        final String templateId = "template-1";
        final TestTemplateResponse response = TestTemplateResponse.builder()
                .id(templateId)
                .name("General Test")
                .build();

        when(retrieveOne.execute(templateId)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/v1/templates/{templateId}", templateId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(templateId))
                .andExpect(jsonPath("$.name").value("General Test"));
    }

    @Test
    void shouldReturnNotFoundWhenTemplateDoesNotExist() throws Exception {
        final String templateId = "template-999";
        when(retrieveOne.execute(templateId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/templates/{templateId}", templateId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Test Template Not Found"));
    }

    @Test
    void shouldUpdateTemplateSuccessfully() throws Exception {
        final String templateId = "template-1";
        final UpdateTestTemplateRequest request = UpdateTestTemplateRequest.builder()
                .name("Updated General Test")
                .description("Updated desc")
                .exercises(List.of(
                        TemplateExerciseRequest.builder()
                                .exerciseTitle("Pushups")
                                .unit(MeasurementUnit.COUNT)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();

        final TestTemplateResponse response = TestTemplateResponse.builder()
                .id(templateId)
                .name("Updated General Test")
                .description("Updated desc")
                .build();

        when(updateUseCase.execute(eq(templateId), any(UpdateTestTemplateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/templates/{templateId}", templateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(templateId))
                .andExpect(jsonPath("$.name").value("Updated General Test"))
                .andExpect(jsonPath("$.description").value("Updated desc"));
    }

    @Test
    void shouldDeleteTemplateSuccessfully() throws Exception {
        final String templateId = "template-1";
        doNothing().when(deleteUseCase).execute(templateId);

        mockMvc.perform(delete("/api/v1/templates/{templateId}", templateId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentTemplate() throws Exception {
        final String templateId = "template-999";
        doThrow(new TestTemplateNotFoundException("Test template not found")).when(deleteUseCase).execute(templateId);

        mockMvc.perform(delete("/api/v1/templates/{templateId}", templateId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Test Template Not Found"));
    }
}
