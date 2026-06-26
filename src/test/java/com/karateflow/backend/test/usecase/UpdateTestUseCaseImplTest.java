package com.karateflow.backend.test.usecase;

import com.karateflow.backend.common.exception.TestExecutionNotFoundException;
import com.karateflow.backend.test.domain.model.MeasurementUnit;
import com.karateflow.backend.test.domain.model.PerformedExercise;
import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import com.karateflow.backend.test.dto.request.PerformedExerciseRequest;
import com.karateflow.backend.test.dto.request.UpdateTestRequest;
import com.karateflow.backend.test.dto.response.TestResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTestUseCaseImplTest {

    @Mock
    private TestExecutionRepository testRepository;

    @InjectMocks
    private UpdateTestUseCaseImpl useCase;

    @Test
    void shouldUpdateTestSuccessfullyAndKeepOriginalDate() {
        // Given
        final String testId = "t1";
        final LocalDateTime originalDate = LocalDateTime.now().minusDays(5);
        final LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(5);

        final TestExecution existingTest = TestExecution.builder()
                .id(testId)
                .athleteId("ath-1")
                .executionDate(originalDate)
                .createdAt(originalCreatedAt)
                .type("Original Type")
                .coachNotes("Original Notes")
                .exercises(List.of(
                        PerformedExercise.builder()
                                .exerciseTitle("Pushups")
                                .result(15.0)
                                .unit(MeasurementUnit.COUNT)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();

        final UpdateTestRequest request = UpdateTestRequest.builder()
                .type("New Type")
                .coachNotes("New Notes")
                .exercises(List.of(
                        PerformedExerciseRequest.builder()
                                .exerciseTitle("Pushups")
                                .result(20.0)
                                .unit(MeasurementUnit.COUNT)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();

        when(testRepository.findById(testId)).thenReturn(Optional.of(existingTest));
        when(testRepository.save(any(TestExecution.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        final TestResponse response = useCase.execute(testId, request);

        // Then
        assertThat(response.getId()).isEqualTo(testId);
        assertThat(response.getType()).isEqualTo("New Type");
        assertThat(response.getCoachNotes()).isEqualTo("New Notes");
        assertThat(response.getExecutionDate()).isEqualTo(originalDate); // Date must be original!
        assertThat(response.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(response.getExercises()).hasSize(1);
        assertThat(response.getExercises().get(0).getResult()).isEqualTo(20.0);

        final ArgumentCaptor<TestExecution> captor = ArgumentCaptor.forClass(TestExecution.class);
        verify(testRepository).save(captor.capture());
        final TestExecution saved = captor.getValue();
        assertThat(saved.getExecutionDate()).isEqualTo(originalDate);
        assertThat(saved.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTest() {
        // Given
        final String testId = "non-existent";
        final UpdateTestRequest request = UpdateTestRequest.builder().exercises(List.of()).build();

        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> useCase.execute(testId, request))
                .isInstanceOf(TestExecutionNotFoundException.class)
                .hasMessageContaining("Cannot update test: Test execution not found with ID: non-existent");

        verify(testRepository).findById(testId);
        verify(testRepository, never()).save(any(TestExecution.class));
    }
}
