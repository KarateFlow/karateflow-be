package com.karateflow.backend.test.domain.port;

import com.karateflow.backend.test.domain.model.TestExecution;

import java.util.List;

public interface TestExecutionRepository {
    TestExecution save(TestExecution testExecution);
    List<TestExecution> findByAthleteId(String athleteId);
}
