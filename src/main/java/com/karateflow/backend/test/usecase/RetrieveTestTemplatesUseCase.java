package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.dto.response.TestTemplateResponse;

import java.util.List;

@FunctionalInterface
public interface RetrieveTestTemplatesUseCase {
    List<TestTemplateResponse> execute();
}
