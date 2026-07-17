package com.karateflow.backend.dashboard.controller;

import com.karateflow.backend.dashboard.domain.model.DashboardSummaryResult;
import com.karateflow.backend.dashboard.usecase.GetDashboardSummaryUseCase;
import com.karateflow.backend.dashboard.dto.response.DashboardSummaryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final GetDashboardSummaryUseCase getDashboardSummaryUseCase;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponseDTO> getSummary() {
        final DashboardSummaryResult result = getDashboardSummaryUseCase.getSummary();

        // Note: For simplicity, we can pass domain objects directly to the DTO if they serialize correctly,
        // or just return the fields that the frontend expects.
        final DashboardSummaryResponseDTO response = DashboardSummaryResponseDTO.builder()
                .totalAthletes(result.getTotalAthletes())
                .totalTests(result.getTotalTests())
                .totalReports(result.getTotalReports())
                .recentTests(result.getRecentTests())
                .recentReports(result.getRecentReports())
                .build();

        return ResponseEntity.ok(response);
    }
}
