package com.karateflow.backend.report.usecase;

import com.karateflow.backend.report.domain.model.Report;
import com.karateflow.backend.report.domain.port.ReportRepository;
import com.karateflow.backend.report.dto.request.ReportPreviewRequestDTO;
import com.karateflow.backend.report.dto.request.ReportSaveRequestDTO;
import com.karateflow.backend.report.dto.response.ReportPreviewResponseDTO;
import com.karateflow.backend.report.dto.response.ReportResponseDTO;
import com.karateflow.backend.report.mapper.ReportMapper;
import com.karateflow.backend.test.domain.model.TestExecution;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaveReportUseCaseImpl implements SaveReportUseCase {

    private static final String TYPE_COMPARISON = "COMPARISON";
    private static final String TYPE_TREND = "TREND";

    private final ReportRepository reportRepository;
    private final GenerateReportPreviewUseCase previewUseCase;
    private final TestExecutionRepository testRepository;
    private final ReportMapper mapper;

    @Override
    public ReportResponseDTO execute(final ReportSaveRequestDTO request) {
        // 1. Generate the preview to get the pre-calculated payload
        final ReportPreviewRequestDTO previewRequest = ReportPreviewRequestDTO.builder()
                .athleteId(request.getAthleteId())
                .analysisType(request.getAnalysisType())
                .testIdA(request.getTestIdA())
                .testIdB(request.getTestIdB())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        final ReportPreviewResponseDTO previewResponse = previewUseCase.execute(previewRequest);

        // 2. Extract test IDs used
        final List<String> testIds = new ArrayList<>();
        if (TYPE_COMPARISON.equalsIgnoreCase(request.getAnalysisType())) {
            if (request.getTestIdA() != null) {
                testIds.add(request.getTestIdA());
            }
            if (request.getTestIdB() != null) {
                testIds.add(request.getTestIdB());
            }
        } else if (TYPE_TREND.equalsIgnoreCase(request.getAnalysisType())) {
            final List<TestExecution> athleteTests = testRepository.findByAthleteId(request.getAthleteId());
            final List<String> filteredIds = athleteTests.stream()
                    .filter(t -> {
                        if (request.getStartDate() != null && t.getExecutionDate().isBefore(request.getStartDate())) {
                            return false;
                        }
                        if (request.getEndDate() != null && t.getExecutionDate().isAfter(request.getEndDate())) {
                            return false;
                        }
                        return true;
                    })
                    .map(TestExecution::getId)
                    .collect(Collectors.toList());
            testIds.addAll(filteredIds);
        }

        // 3. Build the Report domain entity
        final Report report = Report.builder()
                .athleteId(request.getAthleteId())
                .createdAt(LocalDateTime.now())
                .testIds(testIds)
                .payload(mapper.toPayloadDomain(previewResponse))
                .build();

        // 4. Save and map to response DTO
        final Report savedReport = reportRepository.save(report);
        return mapper.toResponse(savedReport);
    }
}
