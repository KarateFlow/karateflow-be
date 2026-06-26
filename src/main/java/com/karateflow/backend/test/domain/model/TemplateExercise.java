package com.karateflow.backend.test.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateExercise {
    private String exerciseTitle;
    private MeasurementUnit unit;
    private Boolean greaterIsBetter;
}
