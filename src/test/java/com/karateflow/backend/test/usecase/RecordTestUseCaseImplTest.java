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
import java.time.LocalDate;
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

    /**
     * Happy path: Verifica che la registrazione di un test generi l'ID e 
     * avvenga correttamente validando le dipendenze associate.
     */
    @Test
    void shouldSuccessfullyRecordTest() {
        // Arrange
        final String athleteId = "athlete-123";
        final CreateTestRequest request = CreateTestRequest.builder()
                .athleteId(athleteId)
                .executionDate(LocalDate.now())
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
        final com.karateflow.backend.test.domain.model.PerformedExercise savedExercise = com.karateflow.backend.test.domain.model.PerformedExercise.builder()
                .exerciseTitle("Squat")
                .result(100.0)
                .unit(MeasurementUnit.KG)
                .greaterIsBetter(true)
                .build();
                
        final TestExecution savedTest = TestExecution.builder()
                .id("test-999")
                .athleteId(athleteId)
                .exercises(List.of(savedExercise))
                .build();

        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
        when(testRepository.save(any(TestExecution.class))).thenReturn(savedTest);

        // Act
        final TestResponse result = useCase.execute(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("test-999");
        assertThat(result.getExercises()).hasSize(1);
        assertThat(result.getExercises().get(0).getExerciseTitle()).isEqualTo("Squat");
        assertThat(result.getExercises().get(0).getResult()).isEqualTo(100.0);
        assertThat(result.getExercises().get(0).getUnit()).isEqualTo(MeasurementUnit.KG);
        assertThat(result.getExercises().get(0).getGreaterIsBetter()).isTrue();
        
        verify(athleteRepository).findById(athleteId);
        org.mockito.ArgumentCaptor<TestExecution> captor = org.mockito.ArgumentCaptor.forClass(TestExecution.class);
        verify(testRepository).save(captor.capture());
        
        TestExecution captured = captor.getValue();
        assertThat(captured.getExercises()).hasSize(1);
        assertThat(captured.getExercises().get(0).getExerciseTitle()).isEqualTo("Squat");
        assertThat(captured.getExercises().get(0).getResult()).isEqualTo(100.0);
        assertThat(captured.getExercises().get(0).getUnit()).isEqualTo(MeasurementUnit.KG);
        assertThat(captured.getExercises().get(0).getGreaterIsBetter()).isTrue();
    }

    /**
     * Sad path: Verifica il lancio dell'eccezione se l'atleta non esiste a sistema
     * durante la registrazione di un test.
     */
    @Test
    void shouldThrowExceptionWhenAthleteNotFound() {
        // Arrange
        final String athleteId = "999";
        final CreateTestRequest request = CreateTestRequest.builder().athleteId(athleteId).build();
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.empty());

        // Act & Then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(AthleteNotFoundException.class)
                .hasMessageContaining(athleteId);
    }
}
