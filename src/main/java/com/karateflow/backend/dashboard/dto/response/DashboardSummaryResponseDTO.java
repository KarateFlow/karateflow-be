package com.karateflow.backend.dashboard.dto.response;

import com.karateflow.backend.report.domain.model.Report;
import com.karateflow.backend.test.domain.model.TestExecution;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardSummaryResponseDTO {
    private long totalAthletes;
    private long totalTests;
    private long totalReports;
    private List<TestExecution> recentTests;
    private List<Report> recentReports;
}
