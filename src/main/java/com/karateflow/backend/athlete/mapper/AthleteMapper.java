package com.karateflow.backend.athlete.mapper;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import org.springframework.stereotype.Component;

@Component
public class AthleteMapper {

    public Athlete toDomain(final AthleteDocument document) {
        if (document == null) {
            return null;
        }
        return Athlete.builder()
                .athleteId(document.getAthleteId())
                .firstName(document.getFirstName())
                .lastName(document.getLastName())
                .birthDate(document.getBirthDate())
                .referenceContact(document.getReferenceContact())
                .medicalNotes(document.getMedicalNotes())
                .createdAt(document.getCreatedAt())
                .build();
    }

    public AthleteDocument toDocument(final Athlete domain) {
        if (domain == null) {
            return null;
        }
        return AthleteDocument.builder()
                .athleteId(domain.getAthleteId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .birthDate(domain.getBirthDate())
                .referenceContact(domain.getReferenceContact())
                .medicalNotes(domain.getMedicalNotes())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public AthleteResponse toResponse(final Athlete domain) {
        if (domain == null) {
            return null;
        }
        return AthleteResponse.builder()
                .athleteId(domain.getAthleteId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .birthDate(domain.getBirthDate())
                .referenceContact(domain.getReferenceContact())
                .medicalNotes(domain.getMedicalNotes())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
