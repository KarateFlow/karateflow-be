package com.karateflow.backend.athlete.mapper;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class per {@link AthleteMapper}.
 */
class AthleteMapperTest {

    private final AthleteMapper mapper = new AthleteMapper();

    /**
     * Testa il caso di fallimento (mutazione) quando viene passato un documento nullo a toDomain.
     * Ritorna null.
     */
    @Test
    void shouldReturnNullWhenToDomainIsCalledWithNullDocument() {
        // Act & Assert
        assertThat(mapper.toDomain(null)).isNull();
    }

    /**
     * Testa il caso di successo (happy path) di mappatura da documento a dominio.
     */
    @Test
    void shouldMapDocumentToDomain() {
        // Arrange
        AthleteDocument doc = AthleteDocument.builder()
                .athleteId("1")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .referenceContact("contact")
                .medicalNotes("notes")
                .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0))
                .build();

        // Act
        Athlete domain = mapper.toDomain(doc);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getAthleteId()).isEqualTo("1");
        assertThat(domain.getFirstName()).isEqualTo("John");
        assertThat(domain.getLastName()).isEqualTo("Doe");
        assertThat(domain.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(domain.getReferenceContact()).isEqualTo("contact");
        assertThat(domain.getMedicalNotes()).isEqualTo("notes");
        assertThat(domain.getCreatedAt()).isEqualTo(LocalDateTime.of(2023, 1, 1, 0, 0));
    }

    /**
     * Testa il caso di fallimento (mutazione) quando viene passato un dominio nullo a toDocument.
     * Ritorna null.
     */
    @Test
    void shouldReturnNullWhenToDocumentIsCalledWithNullDomain() {
        // Act & Assert
        assertThat(mapper.toDocument(null)).isNull();
    }

    /**
     * Testa il caso di successo (happy path) di mappatura da dominio a documento.
     */
    @Test
    void shouldMapDomainToDocument() {
        // Arrange
        Athlete domain = Athlete.builder()
                .athleteId("1")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .referenceContact("contact")
                .medicalNotes("notes")
                .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0))
                .build();

        // Act
        AthleteDocument doc = mapper.toDocument(domain);

        // Assert
        assertThat(doc).isNotNull();
        assertThat(doc.getAthleteId()).isEqualTo("1");
        assertThat(doc.getFirstName()).isEqualTo("John");
        assertThat(doc.getLastName()).isEqualTo("Doe");
        assertThat(doc.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(doc.getReferenceContact()).isEqualTo("contact");
        assertThat(doc.getMedicalNotes()).isEqualTo("notes");
        assertThat(doc.getCreatedAt()).isEqualTo(LocalDateTime.of(2023, 1, 1, 0, 0));
    }

    /**
     * Testa il caso di fallimento (mutazione) quando viene passato un dominio nullo a toResponse.
     * Ritorna null.
     */
    @Test
    void shouldReturnNullWhenToResponseIsCalledWithNullDomain() {
        // Act & Assert
        assertThat(mapper.toResponse(null)).isNull();
    }

    /**
     * Testa il caso di successo (happy path) di mappatura da dominio a risposta.
     */
    @Test
    void shouldMapDomainToResponse() {
        // Arrange
        Athlete domain = Athlete.builder()
                .athleteId("1")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .referenceContact("contact")
                .medicalNotes("notes")
                .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0))
                .build();

        // Act
        AthleteResponse response = mapper.toResponse(domain);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAthleteId()).isEqualTo("1");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(response.getReferenceContact()).isEqualTo("contact");
        assertThat(response.getMedicalNotes()).isEqualTo("notes");
        assertThat(response.getCreatedAt()).isEqualTo(LocalDateTime.of(2023, 1, 1, 0, 0));
    }
}
