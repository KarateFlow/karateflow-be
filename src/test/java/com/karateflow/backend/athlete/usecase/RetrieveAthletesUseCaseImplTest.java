package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import com.karateflow.backend.athlete.mapper.AthleteMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveAthletesUseCaseImplTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private AthleteMapper athleteMapper;

    @InjectMocks
    private RetrieveAthletesUseCaseImpl useCase;

    @Test
    void shouldSuccessfullyRetrieveAllAthletes() {
        // Given
        final Athlete a1 = Athlete.builder().athleteId("1").firstName("Mario").build();
        final Athlete a2 = Athlete.builder().athleteId("2").firstName("Luigi").build();
        final AthleteResponse r1 = AthleteResponse.builder().athleteId("1").firstName("Mario").build();
        final AthleteResponse r2 = AthleteResponse.builder().athleteId("2").firstName("Luigi").build();

        when(athleteRepository.findAll()).thenReturn(List.of(a1, a2));
        when(athleteMapper.toResponse(a1)).thenReturn(r1);
        when(athleteMapper.toResponse(a2)).thenReturn(r2);

        // When
        final List<AthleteResponse> result = useCase.execute();

        // Then
        assertThat(result).hasSize(2).containsExactly(r1, r2);
        verify(athleteRepository).findAll();
        verify(athleteMapper).toResponse(a1);
        verify(athleteMapper).toResponse(a2);
    }

    @Test
    void shouldSuccessfullyRetrieveAthleteById() {
        // Given
        final String athleteId = "123";
        final Athlete athlete = Athlete.builder().athleteId(athleteId).firstName("Mario").build();
        final AthleteResponse response = AthleteResponse.builder().athleteId(athleteId).firstName("Mario").build();

        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
        when(athleteMapper.toResponse(athlete)).thenReturn(response);

        // When
        final Optional<AthleteResponse> result = useCase.execute(athleteId);

        // Then
        assertThat(result).isPresent().contains(response);
        verify(athleteRepository).findById(athleteId);
        verify(athleteMapper).toResponse(athlete);
    }

    @Test
    void shouldReturnEmptyWhenAthleteNotFound() {
        // Given
        final String athleteId = "999";
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.empty());

        // When
        final Optional<AthleteResponse> result = useCase.execute(athleteId);

        // Then
        assertThat(result).isEmpty();
        verify(athleteRepository).findById(athleteId);
    }
}
