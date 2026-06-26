package com.karateflow.backend.report.domain.model;

import com.karateflow.backend.test.domain.model.MeasurementUnit;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExerciseComparison {
    private final String exerciseTitle;
    private final Double resultA;
    private final Double resultB;
    private final Double delta;
    private final Double percentageChange;
    private final MeasurementUnit unit;
    private final Boolean greaterIsBetter;
}
