package com.karateflow.backend.test.controller;

import com.karateflow.backend.test.dto.request.CreateTestRequest;
import com.karateflow.backend.test.dto.response.TestResponse;
import com.karateflow.backend.test.usecase.RecordTestUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tests")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestExecutionController {

    private final RecordTestUseCase recordUseCase;

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
}
