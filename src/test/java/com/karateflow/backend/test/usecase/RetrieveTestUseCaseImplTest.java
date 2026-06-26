package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import com.karateflow.backend.test.dto.response.TestResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveTestUseCaseImplTest {

    @Mock
    private TestExecutionRepository testRepository;

    @InjectMocks
    private RetrieveTestUseCaseImpl useCase;

    @Test
    void shouldRetrieveTestById() {
        // Given
        final String testId = "t1";
        final TestExecution test = TestExecution.builder().id(testId).athleteId("123").exercises(List.of()).build();

        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        // When
        final Optional<TestResponse> result = useCase.execute(testId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("t1");
        verify(testRepository).findById(testId);
    }

    @Test
    void shouldReturnEmptyWhenTestDoesNotExist() {
        // Given
        final String testId = "non-existent";
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        // When
        final Optional<TestResponse> result = useCase.execute(testId);

        // Then
        assertThat(result).isEmpty();
        verify(testRepository).findById(testId);
    }
}
