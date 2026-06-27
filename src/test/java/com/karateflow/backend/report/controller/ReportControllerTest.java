package com.karateflow.backend.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import com.karateflow.backend.common.exception.ReportNotFoundException;
import com.karateflow.backend.common.handler.GlobalExceptionHandler;
import com.karateflow.backend.report.dto.request.ReportPreviewRequestDTO;
import com.karateflow.backend.report.dto.request.ReportSaveRequestDTO;
import com.karateflow.backend.report.dto.response.ReportPreviewResponseDTO;
import com.karateflow.backend.report.dto.response.ReportResponseDTO;
import com.karateflow.backend.report.usecase.DeleteReportUseCase;
import com.karateflow.backend.report.usecase.GenerateReportPreviewUseCase;
import com.karateflow.backend.report.usecase.RetrieveReportsUseCase;
import com.karateflow.backend.report.usecase.SaveReportUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@Import(GlobalExceptionHandler.class)
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private GenerateReportPreviewUseCase generatePreviewUseCase;

    @MockitoBean
    private SaveReportUseCase saveReportUseCase;

    @MockitoBean
    private RetrieveReportsUseCase retrieveReportsUseCase;

    @MockitoBean
    private DeleteReportUseCase deleteReportUseCase;

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

    @Test
    void shouldSaveReportSuccessfully() throws Exception {
        // Given
        final ReportSaveRequestDTO request = ReportSaveRequestDTO.builder()
                .analysisType("COMPARISON")
                .athleteId("athlete-123")
                .testIdA("test-A")
                .testIdB("test-B")
                .build();

        final ReportResponseDTO response = ReportResponseDTO.builder()
                .reportId("report-789")
                .athleteId("athlete-123")
                .createdAt(LocalDateTime.now())
                .testIds(List.of("test-A", "test-B"))
                .payload(ReportPreviewResponseDTO.builder()
                        .athleteId("athlete-123")
                        .analysisType("COMPARISON")
                        .testIdA("test-A")
                        .testIdB("test-B")
                        .lowOverlap(false)
                        .comparisonResults(Collections.emptyList())
                        .build())
                .build();

        when(saveReportUseCase.execute(any(ReportSaveRequestDTO.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportId").value("report-789"))
                .andExpect(jsonPath("$.athleteId").value("athlete-123"))
                .andExpect(jsonPath("$.testIds[0]").value("test-A"))
                .andExpect(jsonPath("$.payload.analysisType").value("COMPARISON"));
    }

    @Test
    void shouldGetReportsByAthleteSuccessfully() throws Exception {
        // Given
        final String athleteId = "athlete-123";
        final ReportResponseDTO report = ReportResponseDTO.builder()
                .reportId("report-789")
                .athleteId(athleteId)
                .createdAt(LocalDateTime.now())
                .testIds(List.of("test-A"))
                .payload(ReportPreviewResponseDTO.builder()
                        .athleteId(athleteId)
                        .analysisType("TREND")
                        .exerciseTrends(Collections.emptyList())
                        .build())
                .build();

        when(retrieveReportsUseCase.execute(athleteId)).thenReturn(List.of(report));

        // When & Then
        mockMvc.perform(get("/api/v1/reports/athlete/{id}", athleteId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reportId").value("report-789"))
                .andExpect(jsonPath("$[0].athleteId").value(athleteId))
                .andExpect(jsonPath("$[0].payload.analysisType").value("TREND"));
    }

    @Test
    void shouldReturn404WhenGetReportsForNonexistentAthlete() throws Exception {
        // Given
        final String athleteId = "nonexistent";
        when(retrieveReportsUseCase.execute(athleteId))
                .thenThrow(new AthleteNotFoundException("Athlete not found with ID: " + athleteId));

        // When & Then
        mockMvc.perform(get("/api/v1/reports/athlete/{id}", athleteId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Athlete Not Found"));
    }

    @Test
    void shouldDeleteReportSuccessfully() throws Exception {
        // Given
        final String reportId = "report-789";
        doNothing().when(deleteReportUseCase).execute(reportId);

        // When & Then
        mockMvc.perform(delete("/api/v1/reports/{id}", reportId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeleteNonexistentReport() throws Exception {
        // Given
        final String reportId = "nonexistent";
        doThrow(new ReportNotFoundException("Report not found with ID: " + reportId))
                .when(deleteReportUseCase).execute(reportId);

        // When & Then
        mockMvc.perform(delete("/api/v1/reports/{id}", reportId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Report Not Found"));
    }
}
