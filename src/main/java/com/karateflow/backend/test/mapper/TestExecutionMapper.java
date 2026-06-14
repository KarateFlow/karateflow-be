package com.karateflow.backend.test.mapper;

import com.karateflow.backend.test.domain.model.PerformedExercise;
import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.persistence.document.PerformedExerciseDocument;
import com.karateflow.backend.test.persistence.document.TestExecutionDocument;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestExecutionMapper {

    public TestExecutionDocument toDocument(final TestExecution domain) {
        if (domain == null) {
            return null;
        }
        return TestExecutionDocument.builder()
                .id(domain.getId())
                .athleteId(domain.getAthleteId())
                .executionDate(domain.getExecutionDate())
                .type(domain.getType())
                .coachNotes(domain.getCoachNotes())
                .exercises(domain.getExercises().stream()
                        .map(this::toExerciseDocument)
                        .collect(Collectors.toList()))
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public TestExecution toDomain(final TestExecutionDocument document) {
        if (document == null) {
            return null;
        }
        return TestExecution.builder()
                .id(document.getId())
                .athleteId(document.getAthleteId())
                .executionDate(document.getExecutionDate())
                .type(document.getType())
                .coachNotes(document.getCoachNotes())
                .exercises(document.getExercises().stream()
                        .map(this::toExerciseDomain)
                        .collect(Collectors.toList()))
                .createdAt(document.getCreatedAt())
                .build();
    }

    private PerformedExerciseDocument toExerciseDocument(final PerformedExercise domain) {
        return PerformedExerciseDocument.builder()
                .exerciseTitle(domain.getExerciseTitle())
                .result(domain.getResult())
                .unit(domain.getUnit())
                .greaterIsBetter(domain.getGreaterIsBetter())
                .build();
    }

    private PerformedExercise toExerciseDomain(final PerformedExerciseDocument document) {
        return PerformedExercise.builder()
                .exerciseTitle(document.getExerciseTitle())
                .result(document.getResult())
                .unit(document.getUnit())
                .greaterIsBetter(document.getGreaterIsBetter())
                .build();
    }
}
