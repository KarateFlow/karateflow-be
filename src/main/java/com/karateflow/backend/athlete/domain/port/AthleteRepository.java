package com.karateflow.backend.athlete.domain.port;

import com.karateflow.backend.athlete.domain.model.Athlete;

import java.util.List;
import java.util.Optional;

public interface AthleteRepository {
    Athlete save(Athlete athlete);
    Optional<Athlete> findById(String athleteId);
    List<Athlete> findAll();
    void deleteById(String athleteId);
}
