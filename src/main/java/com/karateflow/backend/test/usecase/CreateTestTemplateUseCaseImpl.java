package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.domain.model.TemplateExercise;
import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.domain.port.TestTemplateRepository;
import com.karateflow.backend.test.dto.request.CreateTestTemplateRequest;
import com.karateflow.backend.test.dto.request.TemplateExerciseRequest;
import com.karateflow.backend.test.dto.response.TestTemplateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateTestTemplateUseCaseImpl implements CreateTestTemplateUseCase {

    private final TestTemplateRepository repository;

    @Override
    public TestTemplateResponse execute(final CreateTestTemplateRequest request) {
        final TestTemplate domain = TestTemplate.builder()
                .name(request.getName())
                .description(request.getDescription())
                .exercises(request.getExercises().stream()
                        .map(this::toExerciseDomain)
                        .collect(Collectors.toList()))
                .createdAt(LocalDateTime.now())
                .build();

        final TestTemplate saved = repository.save(domain);
        return toResponse(saved);
    }

    private TemplateExercise toExerciseDomain(final TemplateExerciseRequest req) {
        return TemplateExercise.builder()
                .exerciseTitle(req.getExerciseTitle())
                .unit(req.getUnit())
                .greaterIsBetter(req.getGreaterIsBetter())
                .build();
    }

    private TestTemplateResponse toResponse(final TestTemplate domain) {
        return TestTemplateResponse.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .exercises(domain.getExercises().stream()
                        .map(e -> TestTemplateResponse.TemplateExerciseResponse.builder()
                                .exerciseTitle(e.getExerciseTitle())
                                .unit(e.getUnit())
                                .greaterIsBetter(e.getGreaterIsBetter())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
