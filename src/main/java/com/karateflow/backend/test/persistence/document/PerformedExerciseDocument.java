package com.karateflow.backend.test.persistence.document;

import com.karateflow.backend.test.domain.model.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformedExerciseDocument {
    private String exerciseTitle;
    private Double result;
    private MeasurementUnit unit;
    private Boolean greaterIsBetter;
}
