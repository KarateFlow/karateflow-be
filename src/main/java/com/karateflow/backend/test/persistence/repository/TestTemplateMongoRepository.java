package com.karateflow.backend.test.persistence.repository;

import com.karateflow.backend.test.persistence.document.TestTemplateDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTemplateMongoRepository extends MongoRepository<TestTemplateDocument, String> {
}
