package com.karateflow.backend.report.persistence.repository;

import com.karateflow.backend.report.persistence.document.ReportDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportMongoRepository extends MongoRepository<ReportDocument, String> {
    List<ReportDocument> findByAthleteIdOrderByCreatedAtDesc(String athleteId);
}
