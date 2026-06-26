package com.karateflow.backend.report.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestComparisonReport {
    private final String athleteId;
    private final String testIdA;
    private final String testIdB;
    private final Double overlapPercentage;
    private final boolean lowOverlap;
    private final List<ExerciseComparison> comparisons;
}
