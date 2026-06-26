package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.dto.response.TestResponse;
import java.util.Optional;

@FunctionalInterface
public interface RetrieveTestUseCase {
    Optional<TestResponse> execute(String testId);
}
