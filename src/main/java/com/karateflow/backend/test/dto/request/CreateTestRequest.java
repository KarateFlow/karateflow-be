package com.karateflow.backend.test.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTestRequest {
    @NotBlank(message = "Athlete ID is mandatory")
    private String athleteId;

    @NotNull(message = "Execution date is mandatory")
    @PastOrPresent(message = "Execution date cannot be in the future")
    private LocalDateTime executionDate;

    private String type;
    private String coachNotes;

    @NotEmpty(message = "At least one exercise must be performed")
    @Valid
    private List<PerformedExerciseRequest> exercises;
}
