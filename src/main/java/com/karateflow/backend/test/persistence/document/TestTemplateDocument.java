package com.karateflow.backend.test.persistence.document;

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
@Document(collection = "test_templates")
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.ShortVariable"})
public class TestTemplateDocument {
    @Id
    private String id;
    private String name;
    private String description;
    private List<TemplateExerciseDocument> exercises;

    @CreatedDate
    private LocalDateTime createdAt;
}
