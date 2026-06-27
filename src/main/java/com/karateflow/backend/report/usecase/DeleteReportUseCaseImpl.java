package com.karateflow.backend.report.usecase;

import com.karateflow.backend.common.exception.ReportNotFoundException;
import com.karateflow.backend.report.domain.port.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteReportUseCaseImpl implements DeleteReportUseCase {

    private final ReportRepository reportRepository;

    @Override
    public void execute(final String reportId) {
        if (reportRepository.findById(reportId).isEmpty()) {
            throw new ReportNotFoundException("Report not found with ID: " + reportId);
        }
        reportRepository.deleteById(reportId);
    }
}
