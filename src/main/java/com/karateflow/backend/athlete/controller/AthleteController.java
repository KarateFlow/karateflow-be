package com.karateflow.backend.athlete.controller;

import com.karateflow.backend.athlete.dto.request.RecordAthleteRequest;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import com.karateflow.backend.athlete.usecase.RecordAthleteUseCase;
import com.karateflow.backend.athlete.usecase.RetrieveAthletesUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/athletes")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class AthleteController {

    private final RecordAthleteUseCase recordUseCase;
    private final RetrieveAthletesUseCase retrieveUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AthleteResponse recordAthlete(@Valid @RequestBody final RecordAthleteRequest request) {
        if (log.isInfoEnabled()) {
            log.info("Received request to record athlete: {} {}", request.getFirstName(), request.getLastName());
        }
        final AthleteResponse response = recordUseCase.execute(request);
        if (log.isInfoEnabled()) {
            log.info("Athlete recorded successfully with ID: {}", response.getAthleteId());
        }
        return response;
    }

    @GetMapping
    public List<AthleteResponse> retrieveAthletes() {
        log.info("Received request to retrieve all athletes");
        final List<AthleteResponse> response = retrieveUseCase.execute();
        if (log.isInfoEnabled()) {
            log.info("Retrieved {} athletes", response.size());
        }
        return response;
    }
}
