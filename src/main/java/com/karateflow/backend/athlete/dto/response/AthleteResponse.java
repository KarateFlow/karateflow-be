package com.karateflow.backend.athlete.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AthleteResponse {
    private String athleteId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String referenceContact;
    private String medicalNotes;
    private LocalDateTime createdAt;
}
