package com.karateflow.backend.report.mapper;

import com.karateflow.backend.report.domain.model.ExerciseComparison;
import com.karateflow.backend.report.domain.model.ExerciseTrend;
import com.karateflow.backend.report.domain.model.Report;
import com.karateflow.backend.report.domain.model.ReportPayload;
import com.karateflow.backend.report.domain.model.TestComparisonReport;
import com.karateflow.backend.report.domain.model.TestTrendReport;
import com.karateflow.backend.report.domain.model.TrendDataPoint;
import com.karateflow.backend.report.dto.response.ReportPreviewResponseDTO;
import com.karateflow.backend.report.dto.response.ReportResponseDTO;
import com.karateflow.backend.report.persistence.document.ReportDocument;
import com.karateflow.backend.report.persistence.document.ReportPayloadDocument;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class ReportMapper {

    public ReportPreviewResponseDTO toComparisonResponse(final TestComparisonReport report) {
        if (report == null) {
            return null;
        }

        return ReportPreviewResponseDTO.builder()
                .athleteId(report.getAthleteId())
                .analysisType("COMPARISON")
                .testIdA(report.getTestIdA())
                .testIdB(report.getTestIdB())
                .lowOverlap(report.isLowOverlap())
                .overlapPercentage(report.getOverlapPercentage())
                .comparisonResults(report.getComparisons().stream()
                        .map(this::toComparisonDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    public ReportPreviewResponseDTO toTrendResponse(final TestTrendReport report) {
        if (report == null) {
            return null;
        }

        return ReportPreviewResponseDTO.builder()
                .athleteId(report.getAthleteId())
                .analysisType("TREND")
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .exerciseTrends(report.getTrends().stream()
                        .map(this::toTrendDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private ReportPreviewResponseDTO.ExerciseComparisonDTO toComparisonDTO(final ExerciseComparison comp) {
        final String deltaStr = comp.getDelta() != null ? String.format(Locale.US, "%.2f", comp.getDelta()) : "N/A";
        final String pctChangeStr = comp.getPercentageChange() != null ? String.format(Locale.US, "%.2f", comp.getPercentageChange()) : "N/A";

        return ReportPreviewResponseDTO.ExerciseComparisonDTO.builder()
                .exerciseTitle(comp.getExerciseTitle())
                .resultA(comp.getResultA())
                .resultB(comp.getResultB())
                .delta(deltaStr)
                .percentageChange(pctChangeStr)
                .unit(comp.getUnit())
                .greaterIsBetter(comp.getGreaterIsBetter())
                .build();
    }

    private ReportPreviewResponseDTO.ExerciseTrendDTO toTrendDTO(final ExerciseTrend trend) {
        return ReportPreviewResponseDTO.ExerciseTrendDTO.builder()
                .exerciseTitle(trend.getExerciseTitle())
                .unit(trend.getUnit())
                .greaterIsBetter(trend.getGreaterIsBetter())
                .dataPoints(trend.getDataPoints().stream()
                        .map(this::toDataPointDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private ReportPreviewResponseDTO.TrendDataPointDTO toDataPointDTO(final TrendDataPoint dataPoint) {
        return ReportPreviewResponseDTO.TrendDataPointDTO.builder()
                .date(dataPoint.getDate())
                .result(dataPoint.getResult())
                .build();
    }

    // --- Domain -> Persistence mappings ---

    public ReportDocument toDocument(final Report domain) {
        if (domain == null) {
            return null;
        }
        return ReportDocument.builder()
                .reportId(domain.getReportId())
                .athleteId(domain.getAthleteId())
                .createdAt(domain.getCreatedAt())
                .testIds(domain.getTestIds())
                .payload(toPayloadDocument(domain.getPayload()))
                .build();
    }

    private ReportPayloadDocument toPayloadDocument(final ReportPayload payload) {
        if (payload == null) {
            return null;
        }
        return ReportPayloadDocument.builder()
                .athleteId(payload.getAthleteId())
                .analysisType(payload.getAnalysisType())
                .testIdA(payload.getTestIdA())
                .testIdB(payload.getTestIdB())
                .lowOverlap(payload.getLowOverlap())
                .overlapPercentage(payload.getOverlapPercentage())
                .comparisonResults(payload.getComparisonResults() == null ? null : payload.getComparisonResults().stream()
                        .map(c -> ReportPayloadDocument.ExerciseComparisonDetailDocument.builder()
                                .exerciseTitle(c.getExerciseTitle())
                                .resultA(c.getResultA())
                                .resultB(c.getResultB())
                                .delta(c.getDelta())
                                .percentageChange(c.getPercentageChange())
                                .unit(c.getUnit())
                                .greaterIsBetter(c.getGreaterIsBetter())
                                .build())
                        .collect(Collectors.toList()))
                .startDate(payload.getStartDate())
                .endDate(payload.getEndDate())
                .exerciseTrends(payload.getExerciseTrends() == null ? null : payload.getExerciseTrends().stream()
                        .map(t -> ReportPayloadDocument.ExerciseTrendDetailDocument.builder()
                                .exerciseTitle(t.getExerciseTitle())
                                .unit(t.getUnit())
                                .greaterIsBetter(t.getGreaterIsBetter())
                                .dataPoints(t.getDataPoints() == null ? null : t.getDataPoints().stream()
                                        .map(dp -> ReportPayloadDocument.TrendDataPointDetailDocument.builder()
                                                .date(dp.getDate())
                                                .result(dp.getResult())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // --- Persistence -> Domain mappings ---

    public Report toDomain(final ReportDocument document) {
        if (document == null) {
            return null;
        }
        return Report.builder()
                .reportId(document.getReportId())
                .athleteId(document.getAthleteId())
                .createdAt(document.getCreatedAt())
                .testIds(document.getTestIds())
                .payload(toPayloadDomain(document.getPayload()))
                .build();
    }

    private ReportPayload toPayloadDomain(final ReportPayloadDocument payloadDoc) {
        if (payloadDoc == null) {
            return null;
        }
        return ReportPayload.builder()
                .athleteId(payloadDoc.getAthleteId())
                .analysisType(payloadDoc.getAnalysisType())
                .testIdA(payloadDoc.getTestIdA())
                .testIdB(payloadDoc.getTestIdB())
                .lowOverlap(payloadDoc.getLowOverlap())
                .overlapPercentage(payloadDoc.getOverlapPercentage())
                .comparisonResults(payloadDoc.getComparisonResults() == null ? null : payloadDoc.getComparisonResults().stream()
                        .map(c -> ReportPayload.ExerciseComparisonDetail.builder()
                                .exerciseTitle(c.getExerciseTitle())
                                .resultA(c.getResultA())
                                .resultB(c.getResultB())
                                .delta(c.getDelta())
                                .percentageChange(c.getPercentageChange())
                                .unit(c.getUnit())
                                .greaterIsBetter(c.getGreaterIsBetter())
                                .build())
                        .collect(Collectors.toList()))
                .startDate(payloadDoc.getStartDate())
                .endDate(payloadDoc.getEndDate())
                .exerciseTrends(payloadDoc.getExerciseTrends() == null ? null : payloadDoc.getExerciseTrends().stream()
                        .map(t -> ReportPayload.ExerciseTrendDetail.builder()
                                .exerciseTitle(t.getExerciseTitle())
                                .unit(t.getUnit())
                                .greaterIsBetter(t.getGreaterIsBetter())
                                .dataPoints(t.getDataPoints() == null ? null : t.getDataPoints().stream()
                                        .map(dp -> ReportPayload.TrendDataPointDetail.builder()
                                                .date(dp.getDate())
                                                .result(dp.getResult())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // --- Domain -> Response DTO mappings ---

    public ReportResponseDTO toResponse(final Report domain) {
        if (domain == null) {
            return null;
        }
        return ReportResponseDTO.builder()
                .reportId(domain.getReportId())
                .athleteId(domain.getAthleteId())
                .createdAt(domain.getCreatedAt())
                .testIds(domain.getTestIds())
                .payload(toPayloadDTO(domain.getPayload()))
                .build();
    }

    private ReportPreviewResponseDTO toPayloadDTO(final ReportPayload payload) {
        if (payload == null) {
            return null;
        }
        return ReportPreviewResponseDTO.builder()
                .athleteId(payload.getAthleteId())
                .analysisType(payload.getAnalysisType())
                .testIdA(payload.getTestIdA())
                .testIdB(payload.getTestIdB())
                .lowOverlap(payload.getLowOverlap())
                .overlapPercentage(payload.getOverlapPercentage())
                .comparisonResults(payload.getComparisonResults() == null ? null : payload.getComparisonResults().stream()
                        .map(c -> ReportPreviewResponseDTO.ExerciseComparisonDTO.builder()
                                .exerciseTitle(c.getExerciseTitle())
                                .resultA(c.getResultA())
                                .resultB(c.getResultB())
                                .delta(c.getDelta())
                                .percentageChange(c.getPercentageChange())
                                .unit(c.getUnit())
                                .greaterIsBetter(c.getGreaterIsBetter())
                                .build())
                        .collect(Collectors.toList()))
                .startDate(payload.getStartDate())
                .endDate(payload.getEndDate())
                .exerciseTrends(payload.getExerciseTrends() == null ? null : payload.getExerciseTrends().stream()
                        .map(t -> ReportPreviewResponseDTO.ExerciseTrendDTO.builder()
                                .exerciseTitle(t.getExerciseTitle())
                                .unit(t.getUnit())
                                .greaterIsBetter(t.getGreaterIsBetter())
                                .dataPoints(t.getDataPoints() == null ? null : t.getDataPoints().stream()
                                        .map(dp -> ReportPreviewResponseDTO.TrendDataPointDTO.builder()
                                                .date(dp.getDate())
                                                .result(dp.getResult())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // --- Preview Response DTO -> Domain Payload mapping (useful for saveUseCase) ---

    public ReportPayload toPayloadDomain(final ReportPreviewResponseDTO previewResponse) {
        if (previewResponse == null) {
            return null;
        }
        return ReportPayload.builder()
                .athleteId(previewResponse.getAthleteId())
                .analysisType(previewResponse.getAnalysisType())
                .testIdA(previewResponse.getTestIdA())
                .testIdB(previewResponse.getTestIdB())
                .lowOverlap(previewResponse.getLowOverlap())
                .overlapPercentage(previewResponse.getOverlapPercentage())
                .comparisonResults(previewResponse.getComparisonResults() == null ? Collections.emptyList() : previewResponse.getComparisonResults().stream()
                        .map(c -> ReportPayload.ExerciseComparisonDetail.builder()
                                .exerciseTitle(c.getExerciseTitle())
                                .resultA(c.getResultA())
                                .resultB(c.getResultB())
                                .delta(c.getDelta())
                                .percentageChange(c.getPercentageChange())
                                .unit(c.getUnit())
                                .greaterIsBetter(c.getGreaterIsBetter())
                                .build())
                        .collect(Collectors.toList()))
                .startDate(previewResponse.getStartDate())
                .endDate(previewResponse.getEndDate())
                .exerciseTrends(previewResponse.getExerciseTrends() == null ? Collections.emptyList() : previewResponse.getExerciseTrends().stream()
                        .map(t -> ReportPayload.ExerciseTrendDetail.builder()
                                .exerciseTitle(t.getExerciseTitle())
                                .unit(t.getUnit())
                                .greaterIsBetter(t.getGreaterIsBetter())
                                .dataPoints(t.getDataPoints() == null ? Collections.emptyList() : t.getDataPoints().stream()
                                        .map(dp -> ReportPayload.TrendDataPointDetail.builder()
                                                .date(dp.getDate())
                                                .result(dp.getResult())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
