package com.karateflow.backend.report.controller;

import com.karateflow.backend.report.dto.request.ReportPreviewRequestDTO;
import com.karateflow.backend.report.dto.request.ReportSaveRequestDTO;
import com.karateflow.backend.report.dto.response.ReportPreviewResponseDTO;
import com.karateflow.backend.report.dto.response.ReportResponseDTO;
import com.karateflow.backend.report.usecase.DeleteReportUseCase;
import com.karateflow.backend.report.usecase.GenerateReportPreviewUseCase;
import com.karateflow.backend.report.usecase.RetrieveReportsUseCase;
import com.karateflow.backend.report.usecase.SaveReportUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("PMD.LongVariable")
public class ReportController {

    private final GenerateReportPreviewUseCase previewUseCase;
    private final SaveReportUseCase saveReportUseCase;
    private final RetrieveReportsUseCase retrieveReportsUseCase;
    private final DeleteReportUseCase deleteReportUseCase;

    @PostMapping("/preview")
    public ReportPreviewResponseDTO generatePreview(@Valid @RequestBody final ReportPreviewRequestDTO request) {
        if (log.isInfoEnabled()) {
            log.info("Received request to generate report preview of type: {} for athlete ID: {}",
                    request.getAnalysisType(), request.getAthleteId());
        }
        final ReportPreviewResponseDTO response = previewUseCase.execute(request);
        if (log.isInfoEnabled()) {
            log.info("Report preview generated successfully for athlete ID: {}", request.getAthleteId());
        }
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponseDTO saveReport(@Valid @RequestBody final ReportSaveRequestDTO request) {
        if (log.isInfoEnabled()) {
            log.info("Received request to save report of type: {} for athlete ID: {}",
                    request.getAnalysisType(), request.getAthleteId());
        }
        final ReportResponseDTO response = saveReportUseCase.execute(request);
        if (log.isInfoEnabled()) {
            log.info("Report saved successfully with ID: {}", response.getReportId());
        }
        return response;
    }

    @GetMapping("/athlete/{id}")
    public List<ReportResponseDTO> getReportsByAthlete(@PathVariable("id") final String athleteId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to retrieve reports for athlete ID: {}", athleteId);
        }
        final List<ReportResponseDTO> reports = retrieveReportsUseCase.execute(athleteId);
        if (log.isInfoEnabled()) {
            log.info("Retrieved {} reports for athlete ID: {}", reports.size(), athleteId);
        }
        return reports;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReport(@PathVariable("id") final String reportId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to delete report ID: {}", reportId);
        }
        deleteReportUseCase.execute(reportId);
        if (log.isInfoEnabled()) {
            log.info("Report deleted successfully with ID: {}", reportId);
        }
    }
}
