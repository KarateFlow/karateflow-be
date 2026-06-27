package com.karateflow.backend.report.usecase;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import com.karateflow.backend.report.domain.model.Report;
import com.karateflow.backend.report.domain.port.ReportRepository;
import com.karateflow.backend.report.dto.response.ReportResponseDTO;
import com.karateflow.backend.report.mapper.ReportMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class RetrieveReportsUseCaseImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private AthleteRepository athleteRepository;

    @Spy
    private ReportMapper mapper = new ReportMapper();

    @InjectMocks
    private RetrieveReportsUseCaseImpl retrieveReportsUseCase;

    @Test
    void shouldRetrieveReportsSuccessfully() {
        // Given
        final String athleteId = "athlete-123";
        final Athlete athlete = Athlete.builder().athleteId(athleteId).build();
        final Report report = Report.builder()
                .reportId("report-789")
                .athleteId(athleteId)
                .build();

        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
        when(reportRepository.findByAthleteId(athleteId)).thenReturn(List.of(report));

        // When
        final List<ReportResponseDTO> results = retrieveReportsUseCase.execute(athleteId);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getReportId()).isEqualTo("report-789");
    }

    @Test
    void shouldThrowAthleteNotFoundExceptionWhenAthleteDoesNotExist() {
        // Given
        final String athleteId = "nonexistent";
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> retrieveReportsUseCase.execute(athleteId))
                .isInstanceOf(AthleteNotFoundException.class)
                .hasMessageContaining("Athlete not found");
    }
}
