package com.karateflow.backend.athlete.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "athletes")
@CompoundIndex(def = "{'firstName': 1, 'lastName': 1}", unique = true)
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
