package com.karateflow.backend.dashboard.usecase;

import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.dashboard.domain.model.DashboardSummaryResult;
import com.karateflow.backend.report.domain.model.Report;
import com.karateflow.backend.report.domain.port.ReportRepository;
import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDashboardSummaryUseCaseImplTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private TestExecutionRepository testExecutionRepository;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private GetDashboardSummaryUseCaseImpl dashboardService;

    @Test
    void getSummary_returnsAggregatedData() {
        // Arrange
        when(athleteRepository.count()).thenReturn(10L);
        when(testExecutionRepository.count()).thenReturn(20L);
        when(reportRepository.count()).thenReturn(5L);

        List<TestExecution> mockTests = List.of(TestExecution.builder().id("t1").build());
        List<Report> mockReports = List.of(Report.builder().reportId("r1").build());

        when(testExecutionRepository.findTop5ByOrderByExecutionDateDesc()).thenReturn(mockTests);
        when(reportRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(mockReports);

        // Act
        DashboardSummaryResult result = dashboardService.getSummary();

        // Assert
        assertThat(result.getTotalAthletes()).isEqualTo(10L);
        assertThat(result.getTotalTests()).isEqualTo(20L);
        assertThat(result.getTotalReports()).isEqualTo(5L);
        assertThat(result.getRecentTests()).isEqualTo(mockTests);
        assertThat(result.getRecentReports()).isEqualTo(mockReports);
    }
}
