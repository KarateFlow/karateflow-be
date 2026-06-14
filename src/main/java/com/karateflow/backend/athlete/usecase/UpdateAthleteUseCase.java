package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.dto.request.UpdateAthleteRequest;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;

@FunctionalInterface
public interface UpdateAthleteUseCase {
    AthleteResponse execute(String athleteId, UpdateAthleteRequest request);
}
