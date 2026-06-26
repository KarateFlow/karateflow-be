package com.karateflow.backend.test.usecase;

import com.karateflow.backend.common.exception.TestTemplateNotFoundException;
import com.karateflow.backend.test.domain.port.TestTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTestTemplateUseCaseImpl implements DeleteTestTemplateUseCase {

    private final TestTemplateRepository repository;

    @Override
    public void execute(final String templateId) {
        if (repository.findById(templateId).isEmpty()) {
            throw new TestTemplateNotFoundException("Test template not found with ID: " + templateId);
        }
        repository.deleteById(templateId);
    }
}
