package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.athlete.dto.request.RecordAthleteRequest;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import com.karateflow.backend.athlete.mapper.AthleteMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordAthleteUseCaseImplTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private AthleteMapper athleteMapper;

    @InjectMocks
    private RecordAthleteUseCaseImpl useCase;

    @Test
    void shouldSuccessfullyRecordAthlete() {
        // Given
        final RecordAthleteRequest request = RecordAthleteRequest.builder()
                .firstName("Luigi")
                .lastName("Verdi")
                .birthDate(LocalDate.of(1995, 10, 20))
                .referenceContact("contact")
                .medicalNotes("notes")
                .build();

        final Athlete savedAthlete = Athlete.builder()
                .athleteId("id-123")
                .firstName("Luigi")
                .lastName("Verdi")
                .birthDate(LocalDate.of(1995, 10, 20))
                .referenceContact("contact")
                .medicalNotes("notes")
                .createdAt(LocalDateTime.now())
                .build();

        when(athleteRepository.save(any(Athlete.class))).thenReturn(savedAthlete);

        // When
        final AthleteResponse response = useCase.execute(request);

        // Then
        assertNotNull(response);
        assertEquals("id-123", response.getAthleteId());
        assertEquals("Luigi", response.getFirstName());
        assertEquals("Verdi", response.getLastName());
        assertEquals(request.getBirthDate(), response.getBirthDate());

        verify(athleteRepository).save(any(Athlete.class));
    }
}
