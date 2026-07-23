package com.karateflow.backend.report.persistence.adapter;

import com.karateflow.backend.report.domain.model.Report;
import com.karateflow.backend.report.mapper.ReportMapper;
import com.karateflow.backend.report.persistence.document.ReportDocument;
import com.karateflow.backend.report.persistence.repository.ReportMongoRepository;
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
 * Test class per {@link ReportRepositoryAdapter}.
 */
@ExtendWith(MockitoExtension.class)
class ReportRepositoryAdapterTest {

    @Mock
    private ReportMongoRepository mongoRepository;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private ReportRepositoryAdapter adapter;

    /**
     * Testa il caso di salvataggio (happy path) in cui id e createdAt vengono generati.
     */
    @Test
    void shouldSaveReportAndGenerateIdAndCreatedAtIfNull() {
        // Arrange
        Report report = Report.builder().build();
        ReportDocument docWithoutId = ReportDocument.builder().build();

        when(reportMapper.toDocument(report)).thenReturn(docWithoutId);
        when(mongoRepository.save(any(ReportDocument.class))).thenAnswer(i -> i.getArgument(0));
        when(reportMapper.toDomain(any(ReportDocument.class))).thenReturn(report);

        // Act
        adapter.save(report);

        // Assert
        ArgumentCaptor<ReportDocument> captor = ArgumentCaptor.forClass(ReportDocument.class);
        verify(mongoRepository).save(captor.capture());

        ReportDocument savedDoc = captor.getValue();
        assertThat(savedDoc.getReportId()).isNotNull();
        assertThat(savedDoc.getCreatedAt()).isNotNull();
    }

    /**
     * Testa il caso di salvataggio (happy path) in cui id e createdAt preesistenti non vengono sovrascritti.
     */
    @Test
    void shouldNotOverwriteIdAndCreatedAtIfPresent() {
        // Arrange
        Report report = Report.builder().build();
        ReportDocument docWithId = ReportDocument.builder()
                .reportId("existing-id")
                .createdAt(java.time.LocalDateTime.of(2022, 1, 1, 0, 0))
                .build();

        when(reportMapper.toDocument(report)).thenReturn(docWithId);
        when(mongoRepository.save(any(ReportDocument.class))).thenAnswer(i -> i.getArgument(0));
        when(reportMapper.toDomain(any(ReportDocument.class))).thenReturn(report);

        // Act
        adapter.save(report);

        // Assert
        ArgumentCaptor<ReportDocument> captor = ArgumentCaptor.forClass(ReportDocument.class);
        verify(mongoRepository).save(captor.capture());

        ReportDocument savedDoc = captor.getValue();
        assertThat(savedDoc.getReportId()).isEqualTo("existing-id");
        assertThat(savedDoc.getCreatedAt()).isEqualTo(java.time.LocalDateTime.of(2022, 1, 1, 0, 0));
    }

    /**
     * Testa il recupero di un report per ID (happy path).
     */
    @Test
    void shouldFindById() {
        // Arrange
        ReportDocument doc = ReportDocument.builder().reportId("1").build();
        Report report = Report.builder().reportId("1").build();

        when(mongoRepository.findById("1")).thenReturn(Optional.of(doc));
        when(reportMapper.toDomain(doc)).thenReturn(report);

        // Act
        Optional<Report> result = adapter.findById("1");

        // Assert
        assertThat(result).isPresent().contains(report);
    }

    /**
     * Testa il recupero dei report filtrati per ID atleta (happy path).
     */
    @Test
    void shouldFindByAthleteId() {
        // Arrange
        ReportDocument doc = ReportDocument.builder().athleteId("a1").build();
        Report report = Report.builder().athleteId("a1").build();

        when(mongoRepository.findByAthleteIdOrderByCreatedAtDesc("a1")).thenReturn(List.of(doc));
        when(reportMapper.toDomain(doc)).thenReturn(report);

        // Act
        List<Report> result = adapter.findByAthleteId("a1");

        // Assert
        assertThat(result).containsExactly(report);
    }

    /**
     * Testa la cancellazione di un report per ID (happy path).
     */
    @Test
    void shouldDeleteById() {
        // Act
        adapter.deleteById("1");
        
        // Assert
        verify(mongoRepository).deleteById("1");
    }

    /**
     * Testa il calcolo del numero totale di report (happy path).
     */
    @Test
    void shouldCount() {
        // Arrange
        when(mongoRepository.count()).thenReturn(5L);
        
        // Act & Assert
        assertThat(adapter.count()).isEqualTo(5L);
    }

    /**
     * Testa il recupero degli ultimi 5 report ordinati per data (happy path).
     */
    @Test
    void shouldFindTop5ByOrderByCreatedAtDesc() {
        // Arrange
        ReportDocument doc = ReportDocument.builder().reportId("1").build();
        Report report = Report.builder().reportId("1").build();

        when(mongoRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of(doc));
        when(reportMapper.toDomain(doc)).thenReturn(report);

        // Act
        List<Report> result = adapter.findTop5ByOrderByCreatedAtDesc();

        // Assert
        assertThat(result).containsExactly(report);
    }
}
