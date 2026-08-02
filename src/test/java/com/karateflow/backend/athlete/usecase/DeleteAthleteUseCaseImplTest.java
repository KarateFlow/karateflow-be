package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import com.karateflow.backend.report.domain.port.ReportRepository;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAthleteUseCaseImplTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private TestExecutionRepository testExecutionRepository;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private DeleteAthleteUseCaseImpl deleteAthleteUseCase;

    @Test
    void execute_WhenAthleteExists_ShouldCascadeDelete() {
        // Arrange
        String athleteId = "athlete-1";
        Athlete athlete = Athlete.builder()
                .athleteId(athleteId)
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();
                
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(athlete));

        // Act
        deleteAthleteUseCase.execute(athleteId);

        // Assert
        verify(reportRepository).deleteByAthleteId(athleteId);
        verify(testExecutionRepository).deleteByAthleteId(athleteId);
        verify(athleteRepository).deleteById(athleteId);
    }

    @Test
    void execute_WhenAthleteDoesNotExist_ShouldThrowException() {
        // Arrange
        String athleteId = "athlete-1";
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deleteAthleteUseCase.execute(athleteId))
                .isInstanceOf(AthleteNotFoundException.class)
                .hasMessageContaining("Athlete not found with ID: " + athleteId);

        verify(reportRepository, never()).deleteByAthleteId(anyString());
        verify(testExecutionRepository, never()).deleteByAthleteId(anyString());
        verify(athleteRepository, never()).deleteById(anyString());
    }
}
