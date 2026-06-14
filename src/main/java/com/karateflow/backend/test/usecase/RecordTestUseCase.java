package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.dto.request.CreateTestRequest;
import com.karateflow.backend.test.dto.response.TestResponse;

@FunctionalInterface
public interface RecordTestUseCase {
    TestResponse execute(CreateTestRequest request);
}
