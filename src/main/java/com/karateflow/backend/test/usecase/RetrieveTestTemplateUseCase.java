package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.dto.response.TestTemplateResponse;

import java.util.Optional;

@FunctionalInterface
public interface RetrieveTestTemplateUseCase {
    Optional<TestTemplateResponse> execute(String id);
}
