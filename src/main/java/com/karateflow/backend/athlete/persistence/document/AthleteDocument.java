package com.karateflow.backend.athlete.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "athletes")
public class AthleteDocument {
    @Id
    private String athleteId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String referenceContact;
    private String medicalNotes;

    @CreatedDate
    private LocalDateTime createdAt;
}
