package com.karateflow.backend.report.domain.model;

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
public class Report {
    private String reportId;
    private String athleteId;
    private LocalDateTime createdAt;
    private List<String> testIds;
    private ReportPayload payload;
}
