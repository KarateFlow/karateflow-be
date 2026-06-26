package com.karateflow.backend.test.controller;

import com.karateflow.backend.common.exception.TestTemplateNotFoundException;
import com.karateflow.backend.test.dto.request.CreateTestTemplateRequest;
import com.karateflow.backend.test.dto.request.UpdateTestTemplateRequest;
import com.karateflow.backend.test.dto.response.TestTemplateResponse;
import com.karateflow.backend.test.usecase.CreateTestTemplateUseCase;
import com.karateflow.backend.test.usecase.DeleteTestTemplateUseCase;
import com.karateflow.backend.test.usecase.RetrieveTestTemplateUseCase;
import com.karateflow.backend.test.usecase.RetrieveTestTemplatesUseCase;
import com.karateflow.backend.test.usecase.UpdateTestTemplateUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestTemplateController {

    private final CreateTestTemplateUseCase createUseCase;
    private final RetrieveTestTemplatesUseCase retrieveAllUseCase;
    private final RetrieveTestTemplateUseCase retrieveOneUseCase;
    private final UpdateTestTemplateUseCase updateUseCase;
    private final DeleteTestTemplateUseCase deleteUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestTemplateResponse createTemplate(@Valid @RequestBody final CreateTestTemplateRequest request) {
        if (log.isInfoEnabled()) {
            log.info("Received request to create test template: {}", request.getName());
        }
        final TestTemplateResponse response = createUseCase.execute(request);
        if (log.isInfoEnabled()) {
            log.info("Test template created successfully with ID: {}", response.getId());
        }
        return response;
    }

    @GetMapping
    public List<TestTemplateResponse> retrieveTemplates() {
        if (log.isInfoEnabled()) {
            log.info("Received request to retrieve all test templates");
        }
        final List<TestTemplateResponse> response = retrieveAllUseCase.execute();
        if (log.isInfoEnabled()) {
            log.info("Retrieved {} test templates", response.size());
        }
        return response;
    }

    @GetMapping("/{templateId}")
    public TestTemplateResponse getTemplate(@PathVariable final String templateId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to retrieve test template details for ID: {}", templateId);
        }
        return retrieveOneUseCase.execute(templateId)
                .orElseThrow(() -> new TestTemplateNotFoundException("Test template not found with ID: " + templateId));
    }

    @PutMapping("/{templateId}")
    public TestTemplateResponse updateTemplate(
            @PathVariable final String templateId,
            @Valid @RequestBody final UpdateTestTemplateRequest request) {
        if (log.isInfoEnabled()) {
            log.info("Received request to update test template with ID: {}", templateId);
        }
        final TestTemplateResponse response = updateUseCase.execute(templateId, request);
        if (log.isInfoEnabled()) {
            log.info("Test template updated successfully with ID: {}", templateId);
        }
        return response;
    }

    @DeleteMapping("/{templateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemplate(@PathVariable final String templateId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to delete test template with ID: {}", templateId);
        }
        deleteUseCase.execute(templateId);
        if (log.isInfoEnabled()) {
            log.info("Test template deleted successfully with ID: {}", templateId);
        }
    }
}
