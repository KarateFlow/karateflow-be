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
public class TestResponse {
    private String id;
    private String athleteId;
    private LocalDateTime executionDate;
    private String type;
    private String coachNotes;
    private List<PerformedExerciseResponse> exercises;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformedExerciseResponse {
        private String exerciseTitle;
        private Double result;
        private MeasurementUnit unit;
        private Boolean greaterIsBetter;
    }
}
