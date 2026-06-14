package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.athlete.dto.request.UpdateAthleteRequest;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import com.karateflow.backend.athlete.mapper.AthleteMapper;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAthleteUseCaseImpl implements UpdateAthleteUseCase {

    private final AthleteRepository athleteRepository;
    private final AthleteMapper athleteMapper;

    @Override
    public AthleteResponse execute(final String athleteId, final UpdateAthleteRequest request) {
        final Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new AthleteNotFoundException("Athlete not found with ID: " + athleteId));

        athlete.setReferenceContact(request.getReferenceContact());
        athlete.setMedicalNotes(request.getMedicalNotes());

        final Athlete savedAthlete = athleteRepository.save(athlete);
        return athleteMapper.toResponse(savedAthlete);
    }
}
