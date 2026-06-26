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

    @Test
    void shouldRetrieveAllTestTemplates() {
        // Given
        final TestTemplate template = TestTemplate.builder()
                .id("template-1")
                .name("Standard Physical Test")
                .description("Default template")
                .exercises(List.of())
                .build();

        when(repository.findAll()).thenReturn(List.of(template));

        // When
        final List<TestTemplateResponse> result = useCase.execute();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("template-1");
        verify(repository).findAll();
    }
}
