package com.karateflow.backend.report.domain.model;

import com.karateflow.backend.test.domain.model.MeasurementUnit;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ExerciseTrend {
    private final String exerciseTitle;
    private final MeasurementUnit unit;
    private final Boolean greaterIsBetter;
    private final List<TrendDataPoint> dataPoints;
}
