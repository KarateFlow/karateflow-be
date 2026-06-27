package com.karateflow.backend.report.domain.port;

import com.karateflow.backend.report.domain.model.Report;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {
    Report save(Report report);
    Optional<Report> findById(String reportId);
    List<Report> findByAthleteId(String athleteId);
    void deleteById(String reportId);
}
