package com.karateflow.backend.dashboard.usecase;

import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.dashboard.domain.model.DashboardSummaryResult;
import com.karateflow.backend.report.domain.port.ReportRepository;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetDashboardSummaryUseCaseImpl implements GetDashboardSummaryUseCase {

    private final AthleteRepository athleteRepository;
    private final TestExecutionRepository testExecutionRepository;
    private final ReportRepository reportRepository;

    @Override
    public DashboardSummaryResult getSummary() {
        return DashboardSummaryResult.builder()
                .totalAthletes(athleteRepository.count())
                .totalTests(testExecutionRepository.count())
                .totalReports(reportRepository.count())
                .recentTests(testExecutionRepository.findTop5ByOrderByExecutionDateDesc())
                .recentReports(reportRepository.findTop5ByOrderByCreatedAtDesc())
                .build();
    }
}
