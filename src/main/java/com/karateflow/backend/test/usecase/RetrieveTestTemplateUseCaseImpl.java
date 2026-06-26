package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.domain.port.TestTemplateRepository;
import com.karateflow.backend.test.dto.response.TestTemplateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetrieveTestTemplateUseCaseImpl implements RetrieveTestTemplateUseCase {

    private final TestTemplateRepository repository;

    @Override
    public Optional<TestTemplateResponse> execute(final String templateId) {
        return repository.findById(templateId).map(this::toResponse);
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
