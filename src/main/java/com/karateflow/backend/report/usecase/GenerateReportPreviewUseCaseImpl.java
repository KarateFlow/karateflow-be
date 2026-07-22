package com.karateflow.backend.report.usecase;

import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import com.karateflow.backend.common.exception.TestExecutionNotFoundException;
import com.karateflow.backend.report.domain.model.ReportCalculator;
import com.karateflow.backend.report.domain.model.TestComparisonReport;
import com.karateflow.backend.report.domain.model.TestTrendReport;
import com.karateflow.backend.report.dto.request.ReportPreviewRequestDTO;
import com.karateflow.backend.report.dto.response.ReportPreviewResponseDTO;
import com.karateflow.backend.report.mapper.ReportMapper;
import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerateReportPreviewUseCaseImpl implements GenerateReportPreviewUseCase {

    private static final String TYPE_COMP = "COMPARISON";
    private static final String TYPE_TREND = "TREND";

    private final AthleteRepository athleteRepository;
    private final TestExecutionRepository testRepository;
    private final ReportMapper mapper;

    @Override
    public ReportPreviewResponseDTO execute(final ReportPreviewRequestDTO request) {
        // 1. Validate athlete existence
        if (athleteRepository.findById(request.getAthleteId()).isEmpty()) {
            throw new AthleteNotFoundException("Cannot generate report: Athlete not found with ID: " + request.getAthleteId());
        }

        if (TYPE_COMP.equalsIgnoreCase(request.getAnalysisType())) {
            return generateComparisonReport(request);
        } else if (TYPE_TREND.equalsIgnoreCase(request.getAnalysisType())) {
            return generateTrendReport(request);
        } else {
            throw new IllegalArgumentException("Invalid analysis type: " + request.getAnalysisType());
        }
    }

    private ReportPreviewResponseDTO generateComparisonReport(final ReportPreviewRequestDTO request) {
        final TestExecution testA = testRepository.findById(request.getTestIdA())
                .orElseThrow(() -> new TestExecutionNotFoundException("Test not found with ID: " + request.getTestIdA()));
        final TestExecution testB = testRepository.findById(request.getTestIdB())
                .orElseThrow(() -> new TestExecutionNotFoundException("Test not found with ID: " + request.getTestIdB()));

        if (!testA.getAthleteId().equals(request.getAthleteId())) {
            throw new IllegalArgumentException("Test with ID " + request.getTestIdA() + " does not belong to athlete " + request.getAthleteId());
        }
        if (!testB.getAthleteId().equals(request.getAthleteId())) {
            throw new IllegalArgumentException("Test with ID " + request.getTestIdB() + " does not belong to athlete " + request.getAthleteId());
        }

        final TestComparisonReport report = ReportCalculator.compare(testA, testB);
        return mapper.toComparisonResponse(report);
    }

    private ReportPreviewResponseDTO generateTrendReport(final ReportPreviewRequestDTO request) {
        final List<TestExecution> athleteTests = testRepository.findByAthleteId(request.getAthleteId());

        final List<TestExecution> filteredTests = athleteTests.stream()
                .filter(t -> (request.getStartDate() == null || !t.getExecutionDate().isBefore(request.getStartDate()))
                        && (request.getEndDate() == null || !t.getExecutionDate().isAfter(request.getEndDate())))
                .toList();

        final TestTrendReport trendReport = ReportCalculator.calculateTrend(request.getAthleteId(), filteredTests);
        
        final TestTrendReport trendWithDates = TestTrendReport.builder()
                .athleteId(trendReport.getAthleteId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .trends(trendReport.getTrends())
                .build();

        return mapper.toTrendResponse(trendWithDates);
    }
}
