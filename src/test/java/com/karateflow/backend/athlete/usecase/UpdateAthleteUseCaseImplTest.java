package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.athlete.dto.request.UpdateAthleteRequest;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import com.karateflow.backend.athlete.mapper.AthleteMapper;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAthleteUseCaseImplTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private AthleteMapper athleteMapper;

    @InjectMocks
    private UpdateAthleteUseCaseImpl useCase;

    @Test
    void shouldSuccessfullyUpdateAthlete() {
        // Given
        final String athleteId = "123";
        final UpdateAthleteRequest request = UpdateAthleteRequest.builder()
                .referenceContact("New Contact")
                .medicalNotes("New Notes")
                .build();
        
        final Athlete existingAthlete = Athlete.builder()
                .athleteId(athleteId)
                .firstName("Mario")
                .lastName("Rossi")
                .referenceContact("Old Contact")
                .build();

        final Athlete savedAthlete = Athlete.builder()
                .athleteId(athleteId)
                .firstName("Mario")
                .lastName("Rossi")
                .referenceContact("New Contact")
                .medicalNotes("New Notes")
                .build();

        final AthleteResponse response = AthleteResponse.builder()
                .athleteId(athleteId)
                .referenceContact("New Contact")
                .build();

        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(existingAthlete));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(savedAthlete);
        when(athleteMapper.toResponse(savedAthlete)).thenReturn(response);

        // When
        final AthleteResponse result = useCase.execute(athleteId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReferenceContact()).isEqualTo("New Contact");
        verify(athleteRepository).findById(athleteId);
        verify(athleteRepository).save(existingAthlete);
        assertThat(existingAthlete.getReferenceContact()).isEqualTo("New Contact");
        assertThat(existingAthlete.getMedicalNotes()).isEqualTo("New Notes");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentAthlete() {
        // Given
        final String athleteId = "999";
        final UpdateAthleteRequest request = UpdateAthleteRequest.builder().build();
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> useCase.execute(athleteId, request))
                .isInstanceOf(AthleteNotFoundException.class)
                .hasMessageContaining(athleteId);
    }
}
