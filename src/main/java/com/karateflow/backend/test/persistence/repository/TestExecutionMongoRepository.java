package com.karateflow.backend.test.persistence.repository;

import com.karateflow.backend.test.persistence.document.TestExecutionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestExecutionMongoRepository extends MongoRepository<TestExecutionDocument, String> {
    List<TestExecutionDocument> findByAthleteIdOrderByExecutionDateDesc(String athleteId);
}
