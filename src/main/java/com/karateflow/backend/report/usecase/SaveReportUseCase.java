package com.karateflow.backend.report.usecase;

import com.karateflow.backend.report.dto.request.ReportSaveRequestDTO;
import com.karateflow.backend.report.dto.response.ReportResponseDTO;

@FunctionalInterface
public interface SaveReportUseCase {
    ReportResponseDTO execute(ReportSaveRequestDTO request);
}
