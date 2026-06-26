package com.karateflow.backend.test.dto.request;

import com.karateflow.backend.test.domain.model.MeasurementUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateExerciseRequest {
    @NotBlank(message = "Exercise title is required")
    private String exerciseTitle;

    @NotNull(message = "Measurement unit is required")
    private MeasurementUnit unit;

    @NotNull(message = "greaterIsBetter flag is required")
    private Boolean greaterIsBetter;
}
