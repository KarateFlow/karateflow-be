package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import java.util.List;
import java.util.Optional;

public interface RetrieveAthletesUseCase {
    List<AthleteResponse> execute();
    Optional<AthleteResponse> execute(String athleteId);
}
