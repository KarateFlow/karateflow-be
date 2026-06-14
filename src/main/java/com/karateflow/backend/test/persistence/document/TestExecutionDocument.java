package com.karateflow.backend.test.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "test_executions")
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.ShortVariable"})
public class TestExecutionDocument {
    @Id
    private String id;

    @Indexed
    private String athleteId;

    private LocalDateTime executionDate;
    private String type;
    private String coachNotes;
    private List<PerformedExerciseDocument> exercises;

    @CreatedDate
    private LocalDateTime createdAt;
}
