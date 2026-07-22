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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void shouldAllowSavingWhenAthleteIdMatchesExistingOneDuringUpdate() {
        final String athleteId = "existing-id";
        final Athlete athlete = Athlete.builder()
                .athleteId(athleteId)
                .firstName("Mario")
                .lastName("Rossi")
                .build();

        final AthleteDocument existingDocument = AthleteDocument.builder()
                .athleteId(athleteId)
                .firstName("Mario")
                .lastName("Rossi")
                .build();

        final AthleteDocument savedDocument = AthleteDocument.builder()
                .athleteId(athleteId)
                .firstName("Mario")
                .lastName("Rossi")
                .build();

        when(mongoRepository.findByFirstNameAndLastName("Mario", "Rossi")).thenReturn(Optional.of(existingDocument));
        when(athleteMapper.toDocument(athlete)).thenReturn(existingDocument);
        when(mongoRepository.save(any(AthleteDocument.class))).thenReturn(savedDocument);
        when(athleteMapper.toDomain(savedDocument)).thenReturn(athlete);

        final Athlete result = adapter.save(athlete);

        assertThat(result).isNotNull();
        assertThat(result.getAthleteId()).isEqualTo(athleteId);
        verify(mongoRepository).save(any(AthleteDocument.class));
    }

    @Test
    void shouldReturnAllAthletes() {
        // Given
        final AthleteDocument doc1 = AthleteDocument.builder().athleteId("1").build();
        final AthleteDocument doc2 = AthleteDocument.builder().athleteId("2").build();
        final Athlete a1 = Athlete.builder().athleteId("1").build();
        final Athlete a2 = Athlete.builder().athleteId("2").build();

        when(mongoRepository.findAll()).thenReturn(List.of(doc1, doc2));
        when(athleteMapper.toDomain(doc1)).thenReturn(a1);
        when(athleteMapper.toDomain(doc2)).thenReturn(a2);

        // When
        final List<Athlete> result = adapter.findAll();

        // Then
        assertThat(result).hasSize(2).containsExactly(a1, a2);
        verify(mongoRepository).findAll();
    }
}
