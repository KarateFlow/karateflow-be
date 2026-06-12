package com.karateflow.backend.athlete.persistence.adapter;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.mapper.AthleteMapper;
import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import com.karateflow.backend.athlete.persistence.repository.AthleteMongoRepository;
import com.karateflow.backend.common.exception.AthleteAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AthleteRepositoryAdapterTest {

    @Mock
    private AthleteMongoRepository mongoRepository;

    @Mock
    private AthleteMapper athleteMapper;

    @InjectMocks
    private AthleteRepositoryAdapter adapter;

    @Test
    void shouldThrowAthleteAlreadyExistsExceptionWhenDuplicateAthleteExists() {
        Athlete athlete = Athlete.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .birthDate(LocalDate.of(2008, 6, 1))
                .build();

        when(mongoRepository.findByFirstNameAndLastName("Mario", "Rossi"))
                .thenReturn(Optional.of(AthleteDocument.builder()
                        .athleteId("existing-id")
                        .firstName("Mario")
                        .lastName("Rossi")
                        .build()));

        assertThrows(AthleteAlreadyExistsException.class, () -> adapter.save(athlete));
        verify(mongoRepository, never()).save(any(AthleteDocument.class));
    }
}
