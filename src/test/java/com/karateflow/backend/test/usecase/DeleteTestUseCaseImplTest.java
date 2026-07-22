package com.karateflow.backend.test.usecase;

import com.karateflow.backend.common.exception.TestExecutionNotFoundException;
import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTestUseCaseImplTest {

    @Mock
    private TestExecutionRepository testRepository;

    @InjectMocks
    private DeleteTestUseCaseImpl useCase;

    @Test
    void shouldDeleteTestSuccessfully() {
        // Arrange
        final String testId = "t1";
        final TestExecution test = TestExecution.builder().id(testId).exercises(List.of()).build();

        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        // Act
        useCase.execute(testId);

        // Assert
        verify(testRepository).findById(testId);
        verify(testRepository).deleteById(testId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTest() {
        // Arrange
        final String testId = "non-existent";
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        // Act & Then
        assertThatThrownBy(() -> useCase.execute(testId))
                .isInstanceOf(TestExecutionNotFoundException.class)
                .hasMessageContaining("Test execution not found with ID: non-existent");

        verify(testRepository).findById(testId);
        verify(testRepository, never()).deleteById(testId);
    }
}
