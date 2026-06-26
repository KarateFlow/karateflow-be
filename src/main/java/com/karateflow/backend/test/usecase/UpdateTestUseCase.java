package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.dto.request.UpdateTestRequest;
import com.karateflow.backend.test.dto.response.TestResponse;

@FunctionalInterface
public interface UpdateTestUseCase {
    TestResponse execute(String testId, UpdateTestRequest request);
}
