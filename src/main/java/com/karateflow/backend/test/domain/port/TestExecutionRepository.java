package com.karateflow.backend.test.domain.port;

import com.karateflow.backend.test.domain.model.TestExecution;

import java.util.List;
import java.util.Optional;

public interface TestExecutionRepository {
    TestExecution save(TestExecution testExecution);
    List<TestExecution> findByAthleteId(String athleteId);
    Optional<TestExecution> findById(String testId);
    void deleteById(String testId);
}
