package com.karateflow.backend.test.usecase;

import com.karateflow.backend.common.exception.TestExecutionNotFoundException;
import com.karateflow.backend.test.domain.model.PerformedExercise;
import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import com.karateflow.backend.test.dto.request.PerformedExerciseRequest;
import com.karateflow.backend.test.dto.request.UpdateTestRequest;
import com.karateflow.backend.test.dto.response.TestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateTestUseCaseImpl implements UpdateTestUseCase {

    private final TestExecutionRepository testRepository;

    @Override
    public TestResponse execute(final String testId, final UpdateTestRequest request) {
        final TestExecution existing = testRepository.findById(testId)
                .orElseThrow(() -> new TestExecutionNotFoundException("Cannot update test: Test execution not found with ID: " + testId));

        final TestExecution updated = TestExecution.builder()
                .id(existing.getId())
                .athleteId(existing.getAthleteId())
                .executionDate(existing.getExecutionDate()) // invariata
                .type(request.getType())
                .coachNotes(request.getCoachNotes())
                .exercises(request.getExercises().stream()
                        .map(this::toExerciseDomain)
                        .collect(Collectors.toList()))
                .createdAt(existing.getCreatedAt()) // invariata
                .build();

        final TestExecution saved = testRepository.save(updated);
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
