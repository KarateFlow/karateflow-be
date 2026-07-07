package com.karateflow.backend.test.dto.request;

import com.karateflow.backend.test.domain.model.MeasurementUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformedExerciseRequest {
    @NotBlank(message = "Exercise title is mandatory")
    private String exerciseTitle;

    @NotNull(message = "Result is mandatory")
    @PositiveOrZero(message = "Result must be positive or zero")
    private Double result;

    @NotNull(message = "Unit is mandatory")
    private MeasurementUnit unit;

    @NotNull(message = "GreaterIsBetter is mandatory")
    private Boolean greaterIsBetter;
}
