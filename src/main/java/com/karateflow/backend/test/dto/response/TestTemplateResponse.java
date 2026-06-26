package com.karateflow.backend.test.dto.response;

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
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.ShortVariable"})
public class TestTemplateResponse {
    private String id;
    private String name;
    private String description;
    private List<TemplateExerciseResponse> exercises;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateExerciseResponse {
        private String exerciseTitle;
        private MeasurementUnit unit;
        private Boolean greaterIsBetter;
    }
}
