package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.dto.request.UpdateTestTemplateRequest;
import com.karateflow.backend.test.dto.response.TestTemplateResponse;

@FunctionalInterface
public interface UpdateTestTemplateUseCase {
    TestTemplateResponse execute(String id, UpdateTestTemplateRequest request);
}
