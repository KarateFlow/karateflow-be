package com.karateflow.backend.athlete.usecase;

@FunctionalInterface
public interface DeleteAthleteUseCase {
    void execute(String athleteId);
}
