package com.karateflow.backend.athlete.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordAthleteRequest {
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotNull(message = "Birth date is mandatory")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    private String referenceContact;
    private String medicalNotes;
}
