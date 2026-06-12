package com.karateflow.backend.athlete.persistence.repository;

import com.karateflow.backend.BaseIntegrationTest;
import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AthleteMongoRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private AthleteMongoRepository repository;

    @Test
    void shouldSaveAndRetrieveAthlete() {
        // Given
        final AthleteDocument document = AthleteDocument.builder()
                .firstName("Test")
                .lastName("Integration")
                .birthDate(LocalDate.of(2000, 1, 1))
                .referenceContact("Ref")
                .medicalNotes("Healthy")
                .build();

        // When
        final AthleteDocument savedDocument = repository.save(document);

        // Then
        assertNotNull(savedDocument.getAthleteId());

        final var retrievedDocument = repository.findById(savedDocument.getAthleteId());
        assertTrue(retrievedDocument.isPresent());
        assertEquals("Test", retrievedDocument.get().getFirstName());
    }
}
