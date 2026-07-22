package com.karateflow.backend.test.mapper;

import com.karateflow.backend.test.domain.model.TemplateExercise;
import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.persistence.document.TemplateExerciseDocument;
import com.karateflow.backend.test.persistence.document.TestTemplateDocument;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestTemplateMapper {

    public TestTemplateDocument toDocument(final TestTemplate domain) {
        if (domain == null) {
            return null;
        }
        return TestTemplateDocument.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .exercises(domain.getExercises().stream()
                        .map(this::toExerciseDocument)
                        .collect(Collectors.toList()))
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public TestTemplate toDomain(final TestTemplateDocument document) {
        if (document == null) {
            return null;
        }
        return TestTemplate.builder()
                .id(document.getId())
                .name(document.getName())
                .description(document.getDescription())
                .exercises(document.getExercises().stream()
                        .map(this::toExerciseDomain)
                        .collect(Collectors.toList()))
                .createdAt(document.getCreatedAt())
                .build();
    }

    private TemplateExerciseDocument toExerciseDocument(final TemplateExercise domain) {
        if (domain == null) {
            return null;
        }
        return TemplateExerciseDocument.builder()
                .exerciseTitle(domain.getExerciseTitle())
                .unit(domain.getUnit())
                .greaterIsBetter(domain.getGreaterIsBetter())
                .build();
    }

    private TemplateExercise toExerciseDomain(final TemplateExerciseDocument document) {
        if (document == null) {
            return null;
        }
        return TemplateExercise.builder()
                .exerciseTitle(document.getExerciseTitle())
                .unit(document.getUnit())
                .greaterIsBetter(document.getGreaterIsBetter())
                .build();
    }
}
