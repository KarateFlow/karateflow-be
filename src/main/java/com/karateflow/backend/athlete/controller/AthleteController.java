package com.karateflow.backend.athlete.controller;

import com.karateflow.backend.athlete.dto.request.RecordAthleteRequest;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import com.karateflow.backend.athlete.usecase.RecordAthleteUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/athletes")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class AthleteController {

    private final RecordAthleteUseCase useCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AthleteResponse recordAthlete(@Valid @RequestBody final RecordAthleteRequest request) {
        if (log.isInfoEnabled()) {
            log.info("Received request to record athlete: {} {}", request.getFirstName(), request.getLastName());
        }
        final AthleteResponse response = useCase.execute(request);
        if (log.isInfoEnabled()) {
            log.info("Athlete recorded successfully with ID: {}", response.getAthleteId());
        }
        return response;
    }
}
