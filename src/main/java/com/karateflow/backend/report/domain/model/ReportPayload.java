package com.karateflow.backend.report.domain.model;

import com.karateflow.backend.test.domain.model.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportPayload {
    private String athleteId;
    private String analysisType; // "COMPARISON" or "TREND"
    
    // Comparison results (populated if COMPARISON)
    private String testIdA;
    private String testIdB;
    private Boolean lowOverlap;
    private Double overlapPercentage;
    private List<ExerciseComparisonDetail> comparisonResults;
    
    // Trend results (populated if TREND)
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<ExerciseTrendDetail> exerciseTrends;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseComparisonDetail {
        private String exerciseTitle;
        private Double resultA;
        private Double resultB;
        private String delta;
        private String percentageChange;
        private MeasurementUnit unit;
        private Boolean greaterIsBetter;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseTrendDetail {
        private String exerciseTitle;
        private MeasurementUnit unit;
        private Boolean greaterIsBetter;
        private List<TrendDataPointDetail> dataPoints;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendDataPointDetail {
        private LocalDateTime date;
        private Double result;
    }
}
