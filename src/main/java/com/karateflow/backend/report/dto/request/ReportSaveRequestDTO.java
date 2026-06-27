package com.karateflow.backend.report.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSaveRequestDTO {
    @NotBlank(message = "Athlete ID is mandatory")
    private String athleteId;

    @NotBlank(message = "Analysis type is mandatory")
    private String analysisType; // "COMPARISON" or "TREND"
    
    // For COMPARISON
    private String testIdA;
    private String testIdB;
    
    // For TREND
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
