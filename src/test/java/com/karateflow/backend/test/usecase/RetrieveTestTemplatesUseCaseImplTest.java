package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.domain.port.TestTemplateRepository;
import com.karateflow.backend.test.dto.response.TestTemplateResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveTestTemplatesUseCaseImplTest {

    @Mock
    private TestTemplateRepository repository;

    @InjectMocks
    private RetrieveTestTemplatesUseCaseImpl useCase;

    /**
     * Happy path: Verifica il recupero di tutti i template configurati nel sistema.
     */
    @Test
    void shouldRetrieveAllTestTemplates() {
        // Arrange
        final com.karateflow.backend.test.domain.model.TemplateExercise exercise = com.karateflow.backend.test.domain.model.TemplateExercise.builder()
                .exerciseTitle("Squat")
                .unit(com.karateflow.backend.test.domain.model.MeasurementUnit.KG)
                .greaterIsBetter(true)
                .build();
                
        final TestTemplate template = TestTemplate.builder()
                .id("template-1")
                .name("Standard Physical Test")
                .description("Default template")
                .exercises(List.of(exercise))
                .build();

        when(repository.findAll()).thenReturn(List.of(template));

        // Act
        final List<TestTemplateResponse> result = useCase.execute();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("template-1");
        assertThat(result.get(0).getExercises()).hasSize(1);
        assertThat(result.get(0).getExercises().get(0).getExerciseTitle()).isEqualTo("Squat");
        assertThat(result.get(0).getExercises().get(0).getUnit()).isEqualTo(com.karateflow.backend.test.domain.model.MeasurementUnit.KG);
        assertThat(result.get(0).getExercises().get(0).getGreaterIsBetter()).isTrue();
        verify(repository).findAll();
    }
}
