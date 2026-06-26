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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveTestTemplateUseCaseImplTest {

    @Mock
    private TestTemplateRepository repository;

    @InjectMocks
    private RetrieveTestTemplateUseCaseImpl useCase;

    @Test
    void shouldRetrieveTestTemplate() {
        // Given
        final String templateId = "template-1";
        final TestTemplate template = TestTemplate.builder()
                .id(templateId)
                .name("Standard Physical Test")
                .description("Default template")
                .exercises(List.of())
                .build();

        when(repository.findById(templateId)).thenReturn(Optional.of(template));

        // When
        final Optional<TestTemplateResponse> result = useCase.execute(templateId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(templateId);
        verify(repository).findById(templateId);
    }

    @Test
    void shouldReturnEmptyWhenTemplateNotFound() {
        // Given
        final String templateId = "template-999";
        when(repository.findById(templateId)).thenReturn(Optional.empty());

        // When
        final Optional<TestTemplateResponse> result = useCase.execute(templateId);

        // Then
        assertThat(result).isEmpty();
        verify(repository).findById(templateId);
    }
}
