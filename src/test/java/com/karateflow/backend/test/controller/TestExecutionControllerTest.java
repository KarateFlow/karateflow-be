package com.karateflow.backend.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.karateflow.backend.common.handler.GlobalExceptionHandler;
import com.karateflow.backend.test.domain.model.MeasurementUnit;
import com.karateflow.backend.test.dto.request.CreateTestRequest;
import com.karateflow.backend.test.dto.request.PerformedExerciseRequest;
import com.karateflow.backend.test.dto.response.TestResponse;
import com.karateflow.backend.test.usecase.RecordTestUseCase;
import com.karateflow.backend.test.usecase.RetrieveTestsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestExecutionController.class)
@Import(GlobalExceptionHandler.class)
class TestExecutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private RecordTestUseCase recordUseCase;

    @MockitoBean
    private RetrieveTestsUseCase retrieveUseCase;

    @Test
    void shouldRecordTestSuccessfully() throws Exception {
        // Given
        final CreateTestRequest request = CreateTestRequest.builder()
                .athleteId("123")
                .executionDate(LocalDateTime.now())
                .exercises(List.of(
                        PerformedExerciseRequest.builder()
                                .exerciseTitle("Test")
                                .result(10.0)
                                .unit(MeasurementUnit.CM)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();

        final TestResponse response = TestResponse.builder()
                .id("test-id")
                .athleteId("123")
                .build();

        when(recordUseCase.execute(any(CreateTestRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test-id"))
                .andExpect(jsonPath("$.athleteId").value("123"));
    }

    @Test
    void shouldRetrieveTestsSuccessfully() throws Exception {
        // Given
        final String athleteId = "123";
        final List<TestResponse> response = List.of(
                TestResponse.builder().id("t1").athleteId(athleteId).build()
        );

        when(retrieveUseCase.execute(athleteId)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/tests")
                        .param("athleteId", athleteId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("t1"));
    }

    @Test
    void shouldReturnBadRequestWhenMandatoryFieldsAreMissing() throws Exception {
        // Given
        final CreateTestRequest request = CreateTestRequest.builder().build(); // All fields missing

        // When & Then
        mockMvc.perform(post("/api/v1/tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
