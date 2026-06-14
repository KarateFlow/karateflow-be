package com.karateflow.backend.test.usecase;

import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import com.karateflow.backend.test.domain.model.PerformedExercise;
import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import com.karateflow.backend.test.dto.request.CreateTestRequest;
import com.karateflow.backend.test.dto.request.PerformedExerciseRequest;
import com.karateflow.backend.test.dto.response.TestResponse;
import com.karateflow.backend.test.mapper.TestExecutionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordTestUseCaseImpl implements RecordTestUseCase {

    private final TestExecutionRepository testRepository;
    private final AthleteRepository athleteRepository;
    private final TestExecutionMapper mapper;

    @Override
    public TestResponse execute(final CreateTestRequest request) {
        // Validate athlete existence
        if (athleteRepository.findById(request.getAthleteId()).isEmpty()) {
            throw new AthleteNotFoundException("Cannot record test: Athlete not found with ID: " + request.getAthleteId());
        }

        final TestExecution domain = TestExecution.builder()
                .athleteId(request.getAthleteId())
                .executionDate(request.getExecutionDate())
                .type(request.getType())
                .coachNotes(request.getCoachNotes())
                .exercises(request.getExercises().stream()
                        .map(this::toExerciseDomain)
                        .collect(Collectors.toList()))
                .createdAt(LocalDateTime.now())
                .build();

        final TestExecution saved = testRepository.save(domain);
        return toResponse(saved);
    }

    private PerformedExercise toExerciseDomain(final PerformedExerciseRequest req) {
        return PerformedExercise.builder()
                .exerciseTitle(req.getExerciseTitle())
                .result(req.getResult())
                .unit(req.getUnit())
                .greaterIsBetter(req.getGreaterIsBetter())
                .build();
    }

    private TestResponse toResponse(final TestExecution domain) {
        return TestResponse.builder()
                .id(domain.getId())
                .athleteId(domain.getAthleteId())
                .executionDate(domain.getExecutionDate())
                .type(domain.getType())
                .coachNotes(domain.getCoachNotes())
                .exercises(domain.getExercises().stream()
                        .map(e -> TestResponse.PerformedExerciseResponse.builder()
                                .exerciseTitle(e.getExerciseTitle())
                                .result(e.getResult())
                                .unit(e.getUnit())
                                .greaterIsBetter(e.getGreaterIsBetter())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
