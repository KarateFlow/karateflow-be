package com.karateflow.backend.test.usecase;

import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import com.karateflow.backend.test.dto.response.TestResponse;
import com.karateflow.backend.test.mapper.TestExecutionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveTestsUseCaseImplTest {

    @Mock
    private TestExecutionRepository testRepository;

    @Mock
    private TestExecutionMapper mapper;

    @InjectMocks
    private RetrieveTestsUseCaseImpl useCase;

    @Test
    void shouldRetrieveTestsForAthlete() {
        // Given
        final String athleteId = "123";
        final TestExecution t1 = TestExecution.builder().id("t1").athleteId(athleteId).exercises(List.of()).build();
        final TestExecution t2 = TestExecution.builder().id("t2").athleteId(athleteId).exercises(List.of()).build();

        when(testRepository.findByAthleteId(athleteId)).thenReturn(List.of(t1, t2));

        // When
        final List<TestResponse> result = useCase.execute(athleteId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("t1");
        assertThat(result.get(1).getId()).isEqualTo("t2");
        verify(testRepository).findByAthleteId(athleteId);
    }
}
