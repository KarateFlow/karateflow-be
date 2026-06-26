package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import com.karateflow.backend.common.exception.TestExecutionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTestUseCaseImpl implements DeleteTestUseCase {

    private final TestExecutionRepository testRepository;

    @Override
    public void execute(final String testId) {
        if (testRepository.findById(testId).isEmpty()) {
            throw new TestExecutionNotFoundException("Test execution not found with ID: " + testId);
        }
        testRepository.deleteById(testId);
    }
}
