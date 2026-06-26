package com.karateflow.backend.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.karateflow.backend.common.handler.GlobalExceptionHandler;
import com.karateflow.backend.report.dto.request.ReportPreviewRequestDTO;
import com.karateflow.backend.report.dto.response.ReportPreviewResponseDTO;
import com.karateflow.backend.report.usecase.GenerateReportPreviewUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@Import(GlobalExceptionHandler.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private GenerateReportPreviewUseCase generatePreviewUseCase;

    @Test
    void shouldGenerateReportPreviewSuccessfully() throws Exception {
        // Given
        final ReportPreviewRequestDTO request = ReportPreviewRequestDTO.builder()
                .analysisType("COMPARISON")
                .athleteId("athlete-123")
                .testIdA("test-A")
                .testIdB("test-B")
                .build();

        final ReportPreviewResponseDTO response = ReportPreviewResponseDTO.builder()
                .athleteId("athlete-123")
                .analysisType("COMPARISON")
                .lowOverlap(false)
                .comparisonResults(List.of(
                        ReportPreviewResponseDTO.ExerciseComparisonDTO.builder()
                                .exerciseTitle("Pushups")
                                .delta("5.00")
                                .build()
                ))
                .build();

        when(generatePreviewUseCase.execute(any(ReportPreviewRequestDTO.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/reports/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.athleteId").value("athlete-123"))
                .andExpect(jsonPath("$.analysisType").value("COMPARISON"))
                .andExpect(jsonPath("$.lowOverlap").value(false))
                .andExpect(jsonPath("$.comparisonResults[0].exerciseTitle").value("Pushups"))
                .andExpect(jsonPath("$.comparisonResults[0].delta").value("5.00"));
    }
}
