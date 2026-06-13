package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import java.util.List;

@FunctionalInterface
public interface RetrieveAthletesUseCase {
    List<AthleteResponse> execute();
}
