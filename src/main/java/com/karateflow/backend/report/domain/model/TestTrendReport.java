package com.karateflow.backend.report.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestTrendReport {
    private final String athleteId;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final List<ExerciseTrend> trends;
}
