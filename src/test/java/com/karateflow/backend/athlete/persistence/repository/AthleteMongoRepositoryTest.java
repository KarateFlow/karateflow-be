package com.karateflow.backend.athlete.persistence.repository;

import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@Disabled("Requires an active Docker daemon for Testcontainers. Will run in Jenkins CI.")
class AthleteMongoRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

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
