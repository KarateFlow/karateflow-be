package com.karateflow.backend.report.mapper;

import com.karateflow.backend.report.domain.model.ExerciseComparison;
import com.karateflow.backend.report.domain.model.ExerciseTrend;
import com.karateflow.backend.report.domain.model.TestComparisonReport;
import com.karateflow.backend.report.domain.model.TestTrendReport;
import com.karateflow.backend.report.domain.model.TrendDataPoint;
import com.karateflow.backend.report.dto.response.ReportPreviewResponseDTO;
import org.springframework.stereotype.Component;

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
}
