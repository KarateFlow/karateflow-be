package com.karateflow.backend.report.dto.response;

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
public class ReportPreviewResponseDTO {
    private String athleteId;
    private String analysisType; // "COMPARISON" or "TREND"
    
    // Comparison results (populated if COMPARISON)
    private String testIdA;
    private String testIdB;
    private Boolean lowOverlap;
    private Double overlapPercentage;
    private List<ExerciseComparisonDTO> comparisonResults;
    
    // Trend results (populated if TREND)
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<ExerciseTrendDTO> exerciseTrends;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseComparisonDTO {
        private String exerciseTitle;
        private Double resultA; // Can be null if not in Test A
        private Double resultB; // Can be null if not in Test B
        private String delta; // String to support "N/A"
        private String percentageChange; // String to support "N/A"
        private MeasurementUnit unit;
        private Boolean greaterIsBetter;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseTrendDTO {
        private String exerciseTitle;
        private MeasurementUnit unit;
        private Boolean greaterIsBetter;
        private List<TrendDataPointDTO> dataPoints;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendDataPointDTO {
        private LocalDateTime date;
        private Double result;
    }
}
