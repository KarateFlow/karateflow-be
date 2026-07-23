package com.karateflow.backend.report.usecase;

import com.karateflow.backend.report.domain.model.Report;
import com.karateflow.backend.report.domain.port.ReportRepository;
import com.karateflow.backend.report.dto.request.ReportPreviewRequestDTO;
import com.karateflow.backend.report.dto.request.ReportSaveRequestDTO;
import com.karateflow.backend.report.dto.response.ReportPreviewResponseDTO;
import com.karateflow.backend.report.dto.response.ReportResponseDTO;
import com.karateflow.backend.report.mapper.ReportMapper;
import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class SaveReportUseCaseImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private GenerateReportPreviewUseCase previewUseCase;

    @Mock
    private TestExecutionRepository testRepository;

    @Spy
    private ReportMapper mapper = new ReportMapper();

    @InjectMocks
    private SaveReportUseCaseImpl saveReportUseCase;

    /**
     * Happy path: Verifica il corretto salvataggio del report di tipo confronto (Comparison)
     * richiamando l'adapter corrispondente e mappando le strutture.
     */
    @Test
    void shouldSaveComparisonReportSuccessfully() {
        // Arrange
        final String athleteId = "athlete-123";
        final String testIdA = "test-A";
        final String testIdB = "test-B";

        final ReportSaveRequestDTO request = ReportSaveRequestDTO.builder()
                .athleteId(athleteId)
                .analysisType("COMPARISON")
                .testIdA(testIdA)
                .testIdB(testIdB)
                .build();

        final ReportPreviewResponseDTO previewResponse = ReportPreviewResponseDTO.builder()
                .athleteId(athleteId)
                .analysisType("COMPARISON")
                .testIdA(testIdA)
                .testIdB(testIdB)
                .lowOverlap(false)
                .comparisonResults(Collections.emptyList())
                .build();

        when(previewUseCase.execute(any(ReportPreviewRequestDTO.class))).thenReturn(previewResponse);
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            final Report r = invocation.getArgument(0);
            r.setReportId("report-789");
            return r;
        });

        // Act
        final ReportResponseDTO result = saveReportUseCase.execute(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getReportId()).isEqualTo("report-789");
        assertThat(result.getAthleteId()).isEqualTo(athleteId);
        assertThat(result.getTestIds()).containsExactly(testIdA, testIdB);

        final ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());
        final Report capturedReport = reportCaptor.getValue();
        assertThat(capturedReport.getAthleteId()).isEqualTo(athleteId);
        assertThat(capturedReport.getTestIds()).containsExactly(testIdA, testIdB);
        assertThat(capturedReport.getPayload().getAnalysisType()).isEqualTo("COMPARISON");
    }

    /**
     * Happy path: Verifica il corretto salvataggio del report di tipo trend
     * e la corretta mappatura dell'entità prima di passarla al repository.
     */
    @Test
    void shouldSaveTrendReportSuccessfully() {
        // Arrange
        final String athleteId = "athlete-123";
        final LocalDateTime now = LocalDateTime.now();

        final ReportSaveRequestDTO request = ReportSaveRequestDTO.builder()
                .athleteId(athleteId)
                .analysisType("TREND")
                .startDate(now.minusDays(5))
                .endDate(now.plusDays(1))
                .build();

        final ReportPreviewResponseDTO previewResponse = ReportPreviewResponseDTO.builder()
                .athleteId(athleteId)
                .analysisType("TREND")
                .startDate(now.minusDays(5))
                .endDate(now.plusDays(1))
                .exerciseTrends(Collections.emptyList())
                .build();

        final TestExecution testBefore = TestExecution.builder()
                .id("test-before")
                .athleteId(athleteId)
                .executionDate(now.minusDays(10))
                .build();

        final TestExecution testWithin = TestExecution.builder()
                .id("test-within")
                .athleteId(athleteId)
                .executionDate(now.minusDays(2))
                .build();
                
        final TestExecution testAfter = TestExecution.builder()
                .id("test-after")
                .athleteId(athleteId)
                .executionDate(now.plusDays(5))
                .build();

        when(previewUseCase.execute(any(ReportPreviewRequestDTO.class))).thenReturn(previewResponse);
        when(testRepository.findByAthleteId(athleteId)).thenReturn(List.of(testBefore, testWithin, testAfter));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            final Report r = invocation.getArgument(0);
            r.setReportId("report-789");
            return r;
        });

        // Act
        final ReportResponseDTO result = saveReportUseCase.execute(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getReportId()).isEqualTo("report-789");
        assertThat(result.getTestIds()).containsExactly("test-within");

        final ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());
        final Report capturedReport = reportCaptor.getValue();
        assertThat(capturedReport.getAthleteId()).isEqualTo(athleteId);
        assertThat(capturedReport.getTestIds()).containsExactly("test-within");
        assertThat(capturedReport.getPayload().getAnalysisType()).isEqualTo("TREND");
    }
}
