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
public class TestTemplate {
    private String id;
    private String name;
    private String description;
    private List<TemplateExercise> exercises;
    private LocalDateTime createdAt;
}
