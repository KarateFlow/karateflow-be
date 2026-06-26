package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.domain.port.TestTemplateRepository;
import com.karateflow.backend.test.dto.response.TestTemplateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetrieveTestTemplatesUseCaseImpl implements RetrieveTestTemplatesUseCase {

    private final TestTemplateRepository repository;

    @Override
    public List<TestTemplateResponse> execute() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
