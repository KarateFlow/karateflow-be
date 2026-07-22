package com.karateflow.backend.report.usecase;

import com.karateflow.backend.common.exception.ReportNotFoundException;
import com.karateflow.backend.report.domain.model.Report;
import com.karateflow.backend.report.domain.port.ReportRepository;
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
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class DeleteReportUseCaseImplTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private DeleteReportUseCaseImpl deleteReportUseCase;

    @Test
    void shouldDeleteReportSuccessfully() {
        // Arrange
        final String reportId = "report-789";
        final Report report = Report.builder().reportId(reportId).build();
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        // Act
        deleteReportUseCase.execute(reportId);

        // Assert
        verify(reportRepository).deleteById(reportId);
    }

    @Test
    void shouldThrowReportNotFoundExceptionWhenReportDoesNotExist() {
        // Arrange
        final String reportId = "nonexistent";
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // Act / Then
        assertThatThrownBy(() -> deleteReportUseCase.execute(reportId))
                .isInstanceOf(ReportNotFoundException.class)
                .hasMessageContaining("Report not found");
    }
}
