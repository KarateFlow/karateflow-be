package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.dto.request.RecordAthleteRequest;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;

@FunctionalInterface
public interface RecordAthleteUseCase {
    AthleteResponse execute(RecordAthleteRequest request);
}
