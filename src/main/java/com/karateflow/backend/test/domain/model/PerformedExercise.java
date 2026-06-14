package com.karateflow.backend.test.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformedExercise {
    private String exerciseTitle;
    private Double result;
    private MeasurementUnit unit;
    private Boolean greaterIsBetter;
}
