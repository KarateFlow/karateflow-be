package com.karateflow.backend.report.persistence.adapter;

import com.karateflow.backend.report.domain.model.Report;
import com.karateflow.backend.report.domain.port.ReportRepository;
import com.karateflow.backend.report.mapper.ReportMapper;
import com.karateflow.backend.report.persistence.document.ReportDocument;
import com.karateflow.backend.report.persistence.repository.ReportMongoRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReportRepositoryAdapter implements ReportRepository {

    private final ReportMongoRepository mongoRepository;
    private final ReportMapper reportMapper;

    @Override
    public Report save(final Report report) {
        final ReportDocument document = reportMapper.toDocument(report);
        if (document.getReportId() == null) {
            document.setReportId(new ObjectId().toHexString());
        }
        if (document.getCreatedAt() == null) {
            document.setCreatedAt(LocalDateTime.now());
        }

        final ReportDocument savedDocument = mongoRepository.save(document);
        return reportMapper.toDomain(savedDocument);
    }

    @Override
    public Optional<Report> findById(final String reportId) {
        return mongoRepository.findById(reportId)
                .map(reportMapper::toDomain);
    }

    @Override
    public List<Report> findByAthleteId(final String athleteId) {
        return mongoRepository.findByAthleteIdOrderByCreatedAtDesc(athleteId).stream()
                .map(reportMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(final String reportId) {
        mongoRepository.deleteById(reportId);
    }
}
