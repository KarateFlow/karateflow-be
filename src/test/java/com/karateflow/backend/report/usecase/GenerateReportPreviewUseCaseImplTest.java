package com.karateflow.backend.report.usecase;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import com.karateflow.backend.common.exception.TestExecutionNotFoundException;
import com.karateflow.backend.report.dto.request.ReportPreviewRequestDTO;
import com.karateflow.backend.report.dto.response.ReportPreviewResponseDTO;
import com.karateflow.backend.report.mapper.ReportMapper;
import com.karateflow.backend.test.domain.model.MeasurementUnit;
import com.karateflow.backend.test.domain.model.PerformedExercise;
import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateReportPreviewUseCaseImplTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private TestExecutionRepository testRepository;

    @Spy
    private ReportMapper mapper = new ReportMapper();

    @InjectMocks
    private GenerateReportPreviewUseCaseImpl useCase;

    @Test
    void shouldGenerateComparisonSuccessfully() {
        // Given
        final String athleteId = "athlete-123";
        final String testIdA = "test-A";
        final String testIdB = "test-B";

        final ReportPreviewRequestDTO request = ReportPreviewRequestDTO.builder()
                .analysisType("COMPARISON")
                .athleteId(athleteId)
                .testIdA(testIdA)
                .testIdB(testIdB)
                .build();

        final Athlete athlete = Athlete.builder().athleteId(athleteId).build();
        final TestExecution testA = TestExecution.builder()
                .id(testIdA)
                .athleteId(athleteId)
                .exercises(List.of(PerformedExercise.builder().exerciseTitle("Pushups").result(10.0).unit(MeasurementUnit.COUNT).greaterIsBetter(true).build()))
                .build();
        final TestExecution testB = TestExecution.builder()
                .id(testIdB)
                .athleteId(athleteId)
                .exercises(List.of(PerformedExercise.builder().exerciseTitle("Pushups").result(15.0).unit(MeasurementUnit.COUNT).greaterIsBetter(true).build()))
                .build();

        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
        when(testRepository.findById(testIdA)).thenReturn(Optional.of(testA));
        when(testRepository.findById(testIdB)).thenReturn(Optional.of(testB));

        // When
        final ReportPreviewResponseDTO response = useCase.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAnalysisType()).isEqualTo("COMPARISON");
        assertThat(response.getAthleteId()).isEqualTo(athleteId);
        assertThat(response.getTestIdA()).isEqualTo(testIdA);
        assertThat(response.getTestIdB()).isEqualTo(testIdB);
        assertThat(response.getComparisonResults()).hasSize(1);
        assertThat(response.getComparisonResults().get(0).getDelta()).isEqualTo("5.00");
    }

    @Test
    void shouldThrowExceptionWhenAthleteNotFound() {
        // Given
        final ReportPreviewRequestDTO request = ReportPreviewRequestDTO.builder()
                .athleteId("non-existent")
                .analysisType("COMPARISON")
                .build();

        when(athleteRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(AthleteNotFoundException.class)
                .hasMessageContaining("Athlete not found");
    }

    @Test
    void shouldThrowExceptionWhenTestNotFound() {
        // Given
        final String athleteId = "athlete-123";
        final ReportPreviewRequestDTO request = ReportPreviewRequestDTO.builder()
                .athleteId(athleteId)
                .analysisType("COMPARISON")
                .testIdA("non-existent")
                .testIdB("test-B")
                .build();

        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(Athlete.builder().athleteId(athleteId).build()));
        when(testRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(TestExecutionNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenTestsBelongToDifferentAthletes() {
        // Given
        final String athleteId = "athlete-123";
        final ReportPreviewRequestDTO request = ReportPreviewRequestDTO.builder()
                .athleteId(athleteId)
                .analysisType("COMPARISON")
                .testIdA("test-A")
                .testIdB("test-B")
                .build();

        final TestExecution testA = TestExecution.builder().id("test-A").athleteId(athleteId).build();
        final TestExecution testB = TestExecution.builder().id("test-B").athleteId("other-athlete").build();

        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(Athlete.builder().athleteId(athleteId).build()));
        when(testRepository.findById("test-A")).thenReturn(Optional.of(testA));
        when(testRepository.findById("test-B")).thenReturn(Optional.of(testB));

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to athlete");
    }

    @Test
    void shouldGenerateTrendSuccessfullyAndFilterDates() {
        // Given
        final String athleteId = "athlete-123";
        final LocalDateTime now = LocalDateTime.now();

        final ReportPreviewRequestDTO request = ReportPreviewRequestDTO.builder()
                .analysisType("TREND")
                .athleteId(athleteId)
                .startDate(now.minusDays(5))
                .endDate(now.plusDays(1))
                .build();

        final TestExecution tBefore = TestExecution.builder()
                .athleteId(athleteId)
                .executionDate(now.minusDays(10))
                .exercises(List.of(PerformedExercise.builder().exerciseTitle("Pushups").result(5.0).unit(MeasurementUnit.COUNT).greaterIsBetter(true).build()))
                .build();

        final TestExecution tWithin = TestExecution.builder()
                .athleteId(athleteId)
                .executionDate(now.minusDays(2))
                .exercises(List.of(PerformedExercise.builder().exerciseTitle("Pushups").result(10.0).unit(MeasurementUnit.COUNT).greaterIsBetter(true).build()))
                .build();

        final TestExecution tAfter = TestExecution.builder()
                .athleteId(athleteId)
                .executionDate(now.plusDays(5))
                .exercises(List.of(PerformedExercise.builder().exerciseTitle("Pushups").result(15.0).unit(MeasurementUnit.COUNT).greaterIsBetter(true).build()))
                .build();

        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(Athlete.builder().athleteId(athleteId).build()));
        when(testRepository.findByAthleteId(athleteId)).thenReturn(List.of(tBefore, tWithin, tAfter));

        // When
        final ReportPreviewResponseDTO response = useCase.execute(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAnalysisType()).isEqualTo("TREND");
        assertThat(response.getStartDate()).isEqualTo(now.minusDays(5));
        assertThat(response.getEndDate()).isEqualTo(now.plusDays(1));
        assertThat(response.getExerciseTrends()).hasSize(1);
        // Only tWithin should be included in trend points
        assertThat(response.getExerciseTrends().get(0).getDataPoints()).hasSize(1);
        assertThat(response.getExerciseTrends().get(0).getDataPoints().get(0).getResult()).isEqualTo(10.0);
    }
}
