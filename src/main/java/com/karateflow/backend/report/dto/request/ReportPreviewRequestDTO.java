package com.karateflow.backend.report.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportPreviewRequestDTO {
    private String analysisType; // "COMPARISON" or "TREND"
    private String athleteId;
    
    // For COMPARISON
    private String testIdA;
    private String testIdB;
    
    // For TREND
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
