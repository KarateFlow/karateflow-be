package com.karateflow.backend.test.controller;

import com.karateflow.backend.common.exception.TestExecutionNotFoundException;
import com.karateflow.backend.test.dto.request.CreateTestRequest;
import com.karateflow.backend.test.dto.request.UpdateTestRequest;
import com.karateflow.backend.test.dto.response.TestResponse;
import com.karateflow.backend.test.usecase.DeleteTestUseCase;
import com.karateflow.backend.test.usecase.RecordTestUseCase;
import com.karateflow.backend.test.usecase.RetrieveTestUseCase;
import com.karateflow.backend.test.usecase.RetrieveTestsUseCase;
import com.karateflow.backend.test.usecase.UpdateTestUseCase;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tests")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestExecutionController {

    private final RecordTestUseCase recordUseCase;
    private final RetrieveTestsUseCase retrieveUseCase;
    private final RetrieveTestUseCase detailUseCase;
    private final UpdateTestUseCase updateUseCase;
    private final DeleteTestUseCase deleteUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestResponse recordTest(@Valid @RequestBody final CreateTestRequest request) {
        if (log.isInfoEnabled()) {
            log.info("Received request to record test for athlete ID: {}", request.getAthleteId());
        }
        final TestResponse response = recordUseCase.execute(request);
        if (log.isInfoEnabled()) {
            log.info("Test recorded successfully with ID: {} for athlete: {}", response.getId(), response.getAthleteId());
        }
        return response;
    }

    @GetMapping
    public List<TestResponse> retrieveTests(@RequestParam final String athleteId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to retrieve test history for athlete ID: {}", athleteId);
        }
        final List<TestResponse> response = retrieveUseCase.execute(athleteId);
        if (log.isInfoEnabled()) {
            log.info("Retrieved {} test sessions for athlete: {}", response.size(), athleteId);
        }
        return response;
    }

    @GetMapping("/{testId}")
    public TestResponse getTest(@PathVariable final String testId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to retrieve test details for ID: {}", testId);
        }
        return detailUseCase.execute(testId)
                .orElseThrow(() -> new TestExecutionNotFoundException("Test execution not found with ID: " + testId));
    }

    @PutMapping("/{testId}")
    public TestResponse updateTest(
            @PathVariable final String testId,
            @Valid @RequestBody final UpdateTestRequest request) {
        if (log.isInfoEnabled()) {
            log.info("Received request to update test with ID: {}", testId);
        }
        final TestResponse response = updateUseCase.execute(testId, request);
        if (log.isInfoEnabled()) {
            log.info("Test updated successfully with ID: {}", testId);
        }
        return response;
    }

    @DeleteMapping("/{testId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTest(@PathVariable final String testId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to delete test with ID: {}", testId);
        }
        deleteUseCase.execute(testId);
        if (log.isInfoEnabled()) {
            log.info("Test deleted successfully with ID: {}", testId);
        }
    }
}
