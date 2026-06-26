package com.karateflow.backend.report.usecase;

import com.karateflow.backend.report.dto.request.ReportPreviewRequestDTO;
import com.karateflow.backend.report.dto.response.ReportPreviewResponseDTO;

@FunctionalInterface
public interface GenerateReportPreviewUseCase {
    ReportPreviewResponseDTO execute(ReportPreviewRequestDTO request);
}
