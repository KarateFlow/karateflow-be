package com.karateflow.backend.test.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTestTemplateRequest {
    @NotBlank(message = "Template name is required")
    private String name;

    private String description;

    @NotEmpty(message = "Exercises list cannot be empty")
    @Valid
    private List<TemplateExerciseRequest> exercises;
}
