package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.dto.request.CreateTestTemplateRequest;
import com.karateflow.backend.test.dto.response.TestTemplateResponse;

@FunctionalInterface
public interface CreateTestTemplateUseCase {
    TestTemplateResponse execute(CreateTestTemplateRequest request);
}
