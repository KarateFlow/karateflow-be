package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.domain.model.MeasurementUnit;
import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.domain.port.TestTemplateRepository;
import com.karateflow.backend.test.dto.request.CreateTestTemplateRequest;
import com.karateflow.backend.test.dto.request.TemplateExerciseRequest;
import com.karateflow.backend.test.dto.response.TestTemplateResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTestTemplateUseCaseImplTest {

    @Mock
    private TestTemplateRepository repository;

    @InjectMocks
    private CreateTestTemplateUseCaseImpl useCase;

    @Test
    void shouldCreateTestTemplate() {
        // Given
        final CreateTestTemplateRequest request = CreateTestTemplateRequest.builder()
                .name("Standard Physical Test")
                .description("Default template")
                .exercises(List.of(
                        TemplateExerciseRequest.builder()
                                .exerciseTitle("Pushups")
                                .unit(MeasurementUnit.COUNT)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();

        final TestTemplate saved = TestTemplate.builder()
                .id("template-1")
                .name("Standard Physical Test")
                .description("Default template")
                .exercises(List.of())
                .build();

        when(repository.save(any(TestTemplate.class))).thenReturn(saved);

        // When
        final TestTemplateResponse result = useCase.execute(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("template-1");
        verify(repository).save(any(TestTemplate.class));
    }
}
