package com.karateflow.backend.report.controller;

import com.karateflow.backend.report.dto.request.ReportPreviewRequestDTO;
import com.karateflow.backend.report.dto.response.ReportPreviewResponseDTO;
import com.karateflow.backend.report.usecase.GenerateReportPreviewUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final GenerateReportPreviewUseCase previewUseCase;

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
}
