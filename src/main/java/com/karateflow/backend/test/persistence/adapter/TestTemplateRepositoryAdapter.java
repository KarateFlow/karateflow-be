package com.karateflow.backend.test.persistence.adapter;

import com.karateflow.backend.test.domain.model.TestTemplate;
import com.karateflow.backend.test.domain.port.TestTemplateRepository;
import com.karateflow.backend.test.mapper.TestTemplateMapper;
import com.karateflow.backend.test.persistence.document.TestTemplateDocument;
import com.karateflow.backend.test.persistence.repository.TestTemplateMongoRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestTemplateRepositoryAdapter implements TestTemplateRepository {

    private final TestTemplateMongoRepository mongoRepository;
    private final TestTemplateMapper mapper;

    @Override
    public TestTemplate save(final TestTemplate testTemplate) {
        final TestTemplateDocument document = mapper.toDocument(testTemplate);
        if (document.getId() == null) {
            document.setId(new ObjectId().toHexString());
        }
        final TestTemplateDocument saved = mongoRepository.save(document);
        return mapper.toDomain(saved);
    }

    @Override
    public List<TestTemplate> findAll() {
        return mongoRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TestTemplate> findById(final String templateId) {
        return mongoRepository.findById(templateId)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(final String templateId) {
        mongoRepository.deleteById(templateId);
    }
}
