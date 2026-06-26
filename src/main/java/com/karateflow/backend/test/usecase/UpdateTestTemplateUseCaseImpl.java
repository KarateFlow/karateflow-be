package com.karateflow.backend.test.usecase;

import com.karateflow.backend.common.exception.TestTemplateNotFoundException;
import com.karateflow.backend.test.domain.model.TemplateExercise;
import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.domain.port.TestTemplateRepository;
import com.karateflow.backend.test.dto.request.TemplateExerciseRequest;
import com.karateflow.backend.test.dto.request.UpdateTestTemplateRequest;
import com.karateflow.backend.test.dto.response.TestTemplateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateTestTemplateUseCaseImpl implements UpdateTestTemplateUseCase {

    private final TestTemplateRepository repository;

    @Override
    public TestTemplateResponse execute(final String id, final UpdateTestTemplateRequest request) {
        final TestTemplate existing = repository.findById(id)
                .orElseThrow(() -> new TestTemplateNotFoundException("Test template not found with ID: " + id));

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setExercises(request.getExercises().stream()
                .map(this::toExerciseDomain)
                .collect(Collectors.toList()));

        final TestTemplate saved = repository.save(existing);
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
