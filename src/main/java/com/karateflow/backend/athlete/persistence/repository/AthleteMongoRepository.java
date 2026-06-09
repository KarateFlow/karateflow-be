package com.karateflow.backend.athlete.persistence.repository;

import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AthleteMongoRepository extends MongoRepository<AthleteDocument, String> {
}
