package com.karateflow.backend.test.domain.port;

import com.karateflow.backend.test.domain.model.TestTemplate;

import java.util.List;
import java.util.Optional;

public interface TestTemplateRepository {
    TestTemplate save(TestTemplate testTemplate);
    List<TestTemplate> findAll();
    Optional<TestTemplate> findById(String id);
    void deleteById(String id);
}
