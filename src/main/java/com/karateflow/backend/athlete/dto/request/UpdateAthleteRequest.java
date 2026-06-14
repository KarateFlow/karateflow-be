package com.karateflow.backend.athlete.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAthleteRequest {
    private String referenceContact;
    private String medicalNotes;
}
