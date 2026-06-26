package com.karateflow.backend.test.persistence.adapter;

import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import com.karateflow.backend.test.mapper.TestExecutionMapper;
import com.karateflow.backend.test.persistence.document.TestExecutionDocument;
import com.karateflow.backend.test.persistence.repository.TestExecutionMongoRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestExecutionRepositoryAdapter implements TestExecutionRepository {

    private final TestExecutionMongoRepository mongoRepository;
    private final TestExecutionMapper mapper;

    @Override
    public TestExecution save(final TestExecution testExecution) {
        final TestExecutionDocument document = mapper.toDocument(testExecution);
        if (document.getId() == null) {
            document.setId(new ObjectId().toHexString());
        }
        final TestExecutionDocument saved = mongoRepository.save(document);
        return mapper.toDomain(saved);
    }

    @Override
    public List<TestExecution> findByAthleteId(final String athleteId) {
        return mongoRepository.findByAthleteIdOrderByExecutionDateDesc(athleteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TestExecution> findById(final String testId) {
        return mongoRepository.findById(testId)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(final String testId) {
        mongoRepository.deleteById(testId);
    }
}
