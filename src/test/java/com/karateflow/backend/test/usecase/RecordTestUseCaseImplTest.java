package com.karateflow.backend.test.usecase;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import com.karateflow.backend.test.domain.model.MeasurementUnit;
import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import com.karateflow.backend.test.dto.request.CreateTestRequest;
import com.karateflow.backend.test.dto.request.PerformedExerciseRequest;
import com.karateflow.backend.test.dto.response.TestResponse;
import com.karateflow.backend.test.mapper.TestExecutionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordTestUseCaseImplTest {

    @Mock
    private TestExecutionRepository testRepository;

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private TestExecutionMapper mapper;

    @InjectMocks
    private RecordTestUseCaseImpl useCase;

    @Test
    void shouldSuccessfullyRecordTest() {
        // Given
        final String athleteId = "athlete-123";
        final CreateTestRequest request = CreateTestRequest.builder()
                .athleteId(athleteId)
                .executionDate(LocalDateTime.now())
                .exercises(List.of(
                        PerformedExerciseRequest.builder()
                                .exerciseTitle("Squat")
                                .result(100.0)
                                .unit(MeasurementUnit.KG)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();

        final Athlete athlete = Athlete.builder().athleteId(athleteId).build();
        final TestExecution savedTest = TestExecution.builder()
                .id("test-999")
                .athleteId(athleteId)
                .exercises(List.of())
                .build();

        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
        when(testRepository.save(any(TestExecution.class))).thenReturn(savedTest);

        // When
        final TestResponse result = useCase.execute(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("test-999");
        verify(athleteRepository).findById(athleteId);
        verify(testRepository).save(any(TestExecution.class));
    }

    @Test
    void shouldThrowExceptionWhenAthleteNotFound() {
        // Given
        final String athleteId = "999";
        final CreateTestRequest request = CreateTestRequest.builder().athleteId(athleteId).build();
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(AthleteNotFoundException.class)
                .hasMessageContaining(athleteId);
    }
}
