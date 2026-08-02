package com.karateflow.backend.report.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestTrendReport {
    private final String athleteId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<ExerciseTrend> trends;
}
