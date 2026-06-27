package com.karateflow.backend.report.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reports")
public class ReportDocument {
    @Id
    private String reportId;
    private String athleteId;
    @CreatedDate
    private LocalDateTime createdAt;
    private List<String> testIds;
    private ReportPayloadDocument payload;
}
