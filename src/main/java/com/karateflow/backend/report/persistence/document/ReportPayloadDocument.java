package com.karateflow.backend.report.persistence.document;

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
public class ReportPayloadDocument {
    private String athleteId;
    private String analysisType;
    
    // Comparison results
    private String testIdA;
    private String testIdB;
    private Boolean lowOverlap;
    private Double overlapPercentage;
    private List<ExerciseComparisonDetailDocument> comparisonResults;
    
    // Trend results
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<ExerciseTrendDetailDocument> exerciseTrends;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseComparisonDetailDocument {
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
    public static class ExerciseTrendDetailDocument {
        private String exerciseTitle;
        private MeasurementUnit unit;
        private Boolean greaterIsBetter;
        private List<TrendDataPointDetailDocument> dataPoints;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendDataPointDetailDocument {
        private LocalDateTime date;
        private Double result;
    }
}
