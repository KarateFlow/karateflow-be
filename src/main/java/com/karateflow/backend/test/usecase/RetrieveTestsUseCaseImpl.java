package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import com.karateflow.backend.test.dto.response.TestResponse;
import com.karateflow.backend.test.mapper.TestExecutionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrieveTestsUseCaseImpl implements RetrieveTestsUseCase {

    private final TestExecutionRepository testRepository;
    private final TestExecutionMapper mapper;

    @Override
    public List<TestResponse> execute(final String athleteId) {
        return testRepository.findByAthleteId(athleteId).stream()
                .map(this::toResponse)
                .toList();
    }

    private TestResponse toResponse(final com.karateflow.backend.test.domain.model.TestExecution domain) {
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
                        .toList())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
