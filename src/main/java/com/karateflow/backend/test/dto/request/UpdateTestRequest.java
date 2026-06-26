package com.karateflow.backend.test.dto.request;

import jakarta.validation.Valid;
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
public class UpdateTestRequest {
    private String type;
    private String coachNotes;

    @NotEmpty(message = "At least one exercise must be performed")
    @Valid
    private List<PerformedExerciseRequest> exercises;
}
