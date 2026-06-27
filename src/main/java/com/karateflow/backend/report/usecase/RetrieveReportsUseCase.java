package com.karateflow.backend.report.usecase;

import com.karateflow.backend.report.dto.response.ReportResponseDTO;

import java.util.List;

@FunctionalInterface
public interface RetrieveReportsUseCase {
    List<ReportResponseDTO> execute(String athleteId);
}
