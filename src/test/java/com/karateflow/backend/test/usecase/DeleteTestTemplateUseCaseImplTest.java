package com.karateflow.backend.test.usecase;

import com.karateflow.backend.common.exception.TestTemplateNotFoundException;
import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.domain.port.TestTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTestTemplateUseCaseImplTest {

    @Mock
    private TestTemplateRepository repository;

    @InjectMocks
    private DeleteTestTemplateUseCaseImpl useCase;

    @Test
    void shouldDeleteTestTemplate() {
        // Given
        final String templateId = "template-1";
        final TestTemplate template = TestTemplate.builder().id(templateId).build();
        when(repository.findById(templateId)).thenReturn(Optional.of(template));

        // When
        useCase.execute(templateId);

        // Then
        verify(repository).findById(templateId);
        verify(repository).deleteById(templateId);
    }

    @Test
    void shouldThrowExceptionWhenTemplateNotFoundOnDelete() {
        // Given
        final String templateId = "template-999";
        when(repository.findById(templateId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> useCase.execute(templateId))
                .isInstanceOf(TestTemplateNotFoundException.class)
                .hasMessageContaining(templateId);
    }
}
