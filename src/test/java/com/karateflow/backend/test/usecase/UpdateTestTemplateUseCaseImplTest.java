package com.karateflow.backend.test.usecase;

import com.karateflow.backend.common.exception.TestTemplateNotFoundException;
import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.domain.port.TestTemplateRepository;
import com.karateflow.backend.test.dto.request.UpdateTestTemplateRequest;
import com.karateflow.backend.test.dto.response.TestTemplateResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTestTemplateUseCaseImplTest {

    @Mock
    private TestTemplateRepository repository;

    @InjectMocks
    private UpdateTestTemplateUseCaseImpl useCase;

    @Test
    void shouldUpdateTestTemplate() {
        // Given
        final String templateId = "template-1";
        final UpdateTestTemplateRequest request = UpdateTestTemplateRequest.builder()
                .name("Updated Name")
                .description("Updated desc")
                .exercises(List.of())
                .build();

        final TestTemplate existing = TestTemplate.builder()
                .id(templateId)
                .name("Old Name")
                .description("Old desc")
                .exercises(List.of())
                .build();

        final TestTemplate saved = TestTemplate.builder()
                .id(templateId)
                .name("Updated Name")
                .description("Updated desc")
                .exercises(List.of())
                .build();

        when(repository.findById(templateId)).thenReturn(Optional.of(existing));
        when(repository.save(any(TestTemplate.class))).thenReturn(saved);

        // When
        final TestTemplateResponse result = useCase.execute(templateId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        verify(repository).findById(templateId);
        verify(repository).save(any(TestTemplate.class));
    }

    @Test
    void shouldThrowExceptionWhenTemplateNotFoundOnUpdate() {
        // Given
        final String templateId = "template-999";
        final UpdateTestTemplateRequest request = UpdateTestTemplateRequest.builder().build();
        when(repository.findById(templateId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> useCase.execute(templateId, request))
                .isInstanceOf(TestTemplateNotFoundException.class)
                .hasMessageContaining(templateId);
    }
}
