package com.karateflow.backend.test.persistence.adapter;

import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.mapper.TestTemplateMapper;
import com.karateflow.backend.test.persistence.document.TestTemplateDocument;
import com.karateflow.backend.test.persistence.repository.TestTemplateMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class per {@link TestTemplateRepositoryAdapter}.
 */
@ExtendWith(MockitoExtension.class)
class TestTemplateRepositoryAdapterTest {

    @Mock
    private TestTemplateMongoRepository mongoRepository;

    @Mock
    private TestTemplateMapper mapper;

    @InjectMocks
    private TestTemplateRepositoryAdapter adapter;

    /**
     * Testa il caso di salvataggio (happy path) in cui l'id viene generato.
     */
    @Test
    void shouldSaveAndGenerateIdIfNull() {
        // Arrange
        TestTemplate template = TestTemplate.builder().build();
        TestTemplateDocument doc = TestTemplateDocument.builder().build();

        when(mapper.toDocument(template)).thenReturn(doc);
        when(mongoRepository.save(any(TestTemplateDocument.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDomain(any(TestTemplateDocument.class))).thenReturn(template);

        // Act
        adapter.save(template);

        // Assert
        ArgumentCaptor<TestTemplateDocument> captor = ArgumentCaptor.forClass(TestTemplateDocument.class);
        verify(mongoRepository).save(captor.capture());

        assertThat(captor.getValue().getId()).isNotNull();
    }

    /**
     * Testa il caso di salvataggio (happy path) in cui l'id preesistente viene mantenuto.
     */
    @Test
    void shouldSaveAndKeepIdIfPresent() {
        // Arrange
        TestTemplate template = TestTemplate.builder().id("existing-id").build();
        TestTemplateDocument doc = TestTemplateDocument.builder().id("existing-id").build();

        when(mapper.toDocument(template)).thenReturn(doc);
        when(mongoRepository.save(any(TestTemplateDocument.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDomain(any(TestTemplateDocument.class))).thenReturn(template);

        // Act
        adapter.save(template);

        // Assert
        ArgumentCaptor<TestTemplateDocument> captor = ArgumentCaptor.forClass(TestTemplateDocument.class);
        verify(mongoRepository).save(captor.capture());

        assertThat(captor.getValue().getId()).isEqualTo("existing-id");
    }

    /**
     * Testa il recupero di tutti i template (happy path).
     */
    @Test
    void shouldFindAll() {
        // Arrange
        TestTemplateDocument doc = TestTemplateDocument.builder().id("1").build();
        TestTemplate template = TestTemplate.builder().id("1").build();

        when(mongoRepository.findAll()).thenReturn(List.of(doc));
        when(mapper.toDomain(doc)).thenReturn(template);

        // Act
        List<TestTemplate> result = adapter.findAll();

        // Assert
        assertThat(result).containsExactly(template);
    }

    /**
     * Testa il recupero di un template per ID (happy path).
     */
    @Test
    void shouldFindById() {
        // Arrange
        TestTemplateDocument doc = TestTemplateDocument.builder().id("1").build();
        TestTemplate template = TestTemplate.builder().id("1").build();

        when(mongoRepository.findById("1")).thenReturn(Optional.of(doc));
        when(mapper.toDomain(doc)).thenReturn(template);

        // Act
        Optional<TestTemplate> result = adapter.findById("1");

        // Assert
        assertThat(result).isPresent().contains(template);
    }

    /**
     * Testa la cancellazione di un template per ID (happy path).
     */
    @Test
    void shouldDeleteById() {
        // Act
        adapter.deleteById("1");
        
        // Assert
        verify(mongoRepository).deleteById("1");
    }
}
