package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.athlete.dto.request.RecordAthleteRequest;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import com.karateflow.backend.athlete.mapper.AthleteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordAthleteUseCaseImpl implements RecordAthleteUseCase {

    private final AthleteRepository athleteRepository;
    private final AthleteMapper athleteMapper;

    @Override
    public AthleteResponse execute(final RecordAthleteRequest request) {
        final Athlete athlete = Athlete.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .referenceContact(request.getReferenceContact())
                .medicalNotes(request.getMedicalNotes())
                .build();

        final Athlete savedAthlete = athleteRepository.save(athlete);
        return toResponse(savedAthlete);
    }

    private AthleteResponse toResponse(final Athlete athlete) {
        return AthleteResponse.builder()
                .athleteId(athlete.getAthleteId())
                .firstName(athlete.getFirstName())
                .lastName(athlete.getLastName())
                .birthDate(athlete.getBirthDate())
                .referenceContact(athlete.getReferenceContact())
                .medicalNotes(athlete.getMedicalNotes())
                .createdAt(athlete.getCreatedAt())
                .build();
    }
}
