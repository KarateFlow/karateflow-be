package com.karateflow.backend.test.domain.model;

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
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.ShortVariable"})
public class TestExecution {
    private String id;
    private String athleteId;
    private LocalDateTime executionDate;
    private String type;
    private String coachNotes;
    private List<PerformedExercise> exercises;
    private LocalDateTime createdAt;
}
