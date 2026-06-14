package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.dto.response.TestResponse;
import java.util.List;

@FunctionalInterface
public interface RetrieveTestsUseCase {
    List<TestResponse> execute(String athleteId);
}
