package com.karateflow.backend.test.mapper;

import com.karateflow.backend.test.domain.model.TemplateExercise;
import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.persistence.document.TemplateExerciseDocument;
import com.karateflow.backend.test.persistence.document.TestTemplateDocument;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class per {@link TestTemplateMapper}.
 */
class TestTemplateMapperTest {

    private final TestTemplateMapper mapper = new TestTemplateMapper();

    /**
     * Testa la gestione dei valori nulli nel metodo toDocument (mutazione).
     */
    @Test
    void shouldReturnNullWhenToDocumentIsCalledWithNullDomain() {
        // Act & Assert
        assertThat(mapper.toDocument(null)).isNull();
    }

    /**
     * Testa la corretta mappatura da dominio a documento (happy path) e gli elementi nulli (mutazione).
     */
    @Test
    void shouldMapDomainToDocument() {
        // Arrange
        TemplateExercise exercise = TemplateExercise.builder()
                .exerciseTitle("Squat")
                .unit(com.karateflow.backend.test.domain.model.MeasurementUnit.KG)
                .greaterIsBetter(true)
                .build();
        
        List<TemplateExercise> exercises = new ArrayList<>();
        exercises.add(exercise);
        exercises.add(null); // to test the toExerciseDocument null case

        TestTemplate domain = TestTemplate.builder()
                .id("1")
                .name("Template 1")
                .description("Description 1")
                .exercises(exercises)
                .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0))
                .build();

        // Act
        TestTemplateDocument doc = mapper.toDocument(domain);

        // Assert
        assertThat(doc).isNotNull();
        assertThat(doc.getId()).isEqualTo("1");
        assertThat(doc.getName()).isEqualTo("Template 1");
        assertThat(doc.getDescription()).isEqualTo("Description 1");
        assertThat(doc.getCreatedAt()).isEqualTo(LocalDateTime.of(2023, 1, 1, 0, 0));
        
        assertThat(doc.getExercises()).hasSize(2);
        assertThat(doc.getExercises().get(0).getExerciseTitle()).isEqualTo("Squat");
        assertThat(doc.getExercises().get(0).getUnit()).isEqualTo(com.karateflow.backend.test.domain.model.MeasurementUnit.KG);
        assertThat(doc.getExercises().get(0).getGreaterIsBetter()).isTrue();
        assertThat(doc.getExercises().get(1)).isNull();
    }

    /**
     * Testa la gestione dei valori nulli nel metodo toDomain (mutazione).
     */
    @Test
    void shouldReturnNullWhenToDomainIsCalledWithNullDocument() {
        // Act & Assert
        assertThat(mapper.toDomain(null)).isNull();
    }

    /**
     * Testa la corretta mappatura da documento a dominio (happy path) e gli elementi nulli (mutazione).
     */
    @Test
    void shouldMapDocumentToDomain() {
        // Arrange
        TemplateExerciseDocument exerciseDoc = TemplateExerciseDocument.builder()
                .exerciseTitle("Squat")
                .unit(com.karateflow.backend.test.domain.model.MeasurementUnit.KG)
                .greaterIsBetter(true)
                .build();

        List<TemplateExerciseDocument> exercises = new ArrayList<>();
        exercises.add(exerciseDoc);
        exercises.add(null); // to test the toExerciseDomain null case

        TestTemplateDocument doc = TestTemplateDocument.builder()
                .id("1")
                .name("Template 1")
                .description("Description 1")
                .exercises(exercises)
                .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0))
                .build();

        // Act
        TestTemplate domain = mapper.toDomain(doc);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo("1");
        assertThat(domain.getName()).isEqualTo("Template 1");
        assertThat(domain.getDescription()).isEqualTo("Description 1");
        assertThat(domain.getCreatedAt()).isEqualTo(LocalDateTime.of(2023, 1, 1, 0, 0));
        
        assertThat(domain.getExercises()).hasSize(2);
        assertThat(domain.getExercises().get(0).getExerciseTitle()).isEqualTo("Squat");
        assertThat(domain.getExercises().get(0).getUnit()).isEqualTo(com.karateflow.backend.test.domain.model.MeasurementUnit.KG);
        assertThat(domain.getExercises().get(0).getGreaterIsBetter()).isTrue();
        assertThat(domain.getExercises().get(1)).isNull();
    }
}
