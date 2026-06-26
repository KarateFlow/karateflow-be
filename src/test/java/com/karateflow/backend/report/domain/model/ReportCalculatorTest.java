package com.karateflow.backend.report.domain.model;

import com.karateflow.backend.test.domain.model.MeasurementUnit;
import com.karateflow.backend.test.domain.model.PerformedExercise;
import com.karateflow.backend.test.domain.model.TestExecution;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReportCalculatorTest {

    @Test
    void shouldCompareTestsSuccessfully() {
        // Given
        final String athleteId = "athlete-123";
        final PerformedExercise ex1A = PerformedExercise.builder()
                .exerciseTitle("Pushups")
                .result(20.0)
                .unit(MeasurementUnit.COUNT)
                .greaterIsBetter(true)
                .build();
        final PerformedExercise ex2A = PerformedExercise.builder()
                .exerciseTitle("Plank")
                .result(60.0)
                .unit(MeasurementUnit.SEC)
                .greaterIsBetter(true)
                .build();

        final PerformedExercise ex1B = PerformedExercise.builder()
                .exerciseTitle("Pushups")
                .result(25.0)
                .unit(MeasurementUnit.COUNT)
                .greaterIsBetter(true)
                .build();
        final PerformedExercise ex3B = PerformedExercise.builder()
                .exerciseTitle("Squats")
                .result(30.0)
                .unit(MeasurementUnit.COUNT)
                .greaterIsBetter(true)
                .build();

        final TestExecution testA = TestExecution.builder()
                .id("test-A")
                .athleteId(athleteId)
                .executionDate(LocalDateTime.now().minusDays(10))
                .exercises(List.of(ex1A, ex2A))
                .build();

        final TestExecution testB = TestExecution.builder()
                .id("test-B")
                .athleteId(athleteId)
                .executionDate(LocalDateTime.now())
                .exercises(List.of(ex1B, ex3B))
                .build();

        // When
        final TestComparisonReport report = ReportCalculator.compare(testA, testB);

        // Then
        assertThat(report.getAthleteId()).isEqualTo(athleteId);
        assertThat(report.getTestIdA()).isEqualTo("test-A");
        assertThat(report.getTestIdB()).isEqualTo("test-B");

        // 3 unique exercises: Pushups (shared), Plank (A only), Squats (B only)
        // Shared is 1, unique union is 3. 1 / 3 = 33.3% -> not low overlap (since >= 30%)
        assertThat(report.getOverlapPercentage()).isCloseTo(33.33, org.assertj.core.api.Assertions.within(0.01));
        assertThat(report.isLowOverlap()).isFalse();

        assertThat(report.getComparisons()).hasSize(3);

        // Check Pushups
        final ExerciseComparison pushups = report.getComparisons().stream()
                .filter(c -> c.getExerciseTitle().equals("Pushups"))
                .findFirst().orElseThrow();
        assertThat(pushups.getResultA()).isEqualTo(20.0);
        assertThat(pushups.getResultB()).isEqualTo(25.0);
        assertThat(pushups.getDelta()).isEqualTo(5.0);
        assertThat(pushups.getPercentageChange()).isEqualTo(25.0); // (25-20)/20 * 100

        // Check Plank (A only)
        final ExerciseComparison plank = report.getComparisons().stream()
                .filter(c -> c.getExerciseTitle().equals("Plank"))
                .findFirst().orElseThrow();
        assertThat(plank.getResultA()).isEqualTo(60.0);
        assertThat(plank.getResultB()).isNull();
        assertThat(plank.getDelta()).isNull();
        assertThat(plank.getPercentageChange()).isNull();

        // Check Squats (B only)
        final ExerciseComparison squats = report.getComparisons().stream()
                .filter(c -> c.getExerciseTitle().equals("Squats"))
                .findFirst().orElseThrow();
        assertThat(squats.getResultA()).isNull();
        assertThat(squats.getResultB()).isEqualTo(30.0);
        assertThat(squats.getDelta()).isNull();
        assertThat(squats.getPercentageChange()).isNull();
    }

    @Test
    void shouldFlagLowOverlap() {
        // Given
        final String athleteId = "athlete-123";
        final TestExecution testA = TestExecution.builder()
                .id("test-A")
                .athleteId(athleteId)
                .exercises(List.of(
                        PerformedExercise.builder().exerciseTitle("Ex1").result(10.0).build(),
                        PerformedExercise.builder().exerciseTitle("Ex2").result(10.0).build(),
                        PerformedExercise.builder().exerciseTitle("Ex3").result(10.0).build()
                ))
                .build();

        final TestExecution testB = TestExecution.builder()
                .id("test-B")
                .athleteId(athleteId)
                .exercises(List.of(
                        PerformedExercise.builder().exerciseTitle("Ex4").result(10.0).build(),
                        PerformedExercise.builder().exerciseTitle("Ex5").result(10.0).build()
                ))
                .build();

        // When
        final TestComparisonReport report = ReportCalculator.compare(testA, testB);

        // Then
        // 0 shared, 5 total. Overlap is 0.0% -> lowOverlap should be true.
        assertThat(report.getOverlapPercentage()).isEqualTo(0.0);
        assertThat(report.isLowOverlap()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenComparingDifferentAthletes() {
        // Given
        final TestExecution testA = TestExecution.builder().id("A").athleteId("athlete-1").build();
        final TestExecution testB = TestExecution.builder().id("B").athleteId("athlete-2").build();

        // When/Then
        assertThatThrownBy(() -> ReportCalculator.compare(testA, testB))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot compare tests of different athletes");
    }

    @Test
    void shouldHandleDivisionByZeroInPercentage() {
        // Given
        final String athleteId = "athlete-123";
        final TestExecution testA = TestExecution.builder()
                .id("test-A")
                .athleteId(athleteId)
                .exercises(List.of(PerformedExercise.builder().exerciseTitle("Ex1").result(0.0).build()))
                .build();

        final TestExecution testB = TestExecution.builder()
                .id("test-B")
                .athleteId(athleteId)
                .exercises(List.of(PerformedExercise.builder().exerciseTitle("Ex1").result(10.0).build()))
                .build();

        // When
        final TestComparisonReport report = ReportCalculator.compare(testA, testB);

        // Then
        final ExerciseComparison comp = report.getComparisons().get(0);
        assertThat(comp.getDelta()).isEqualTo(10.0);
        assertThat(comp.getPercentageChange()).isEqualTo(0.0);
    }

    @Test
    void shouldCalculateTrendsChronologically() {
        // Given
        final String athleteId = "athlete-123";
        final LocalDateTime date1 = LocalDateTime.now().minusDays(10);
        final LocalDateTime date2 = LocalDateTime.now().minusDays(5);
        final LocalDateTime date3 = LocalDateTime.now();

        final TestExecution test3 = TestExecution.builder()
                .athleteId(athleteId)
                .executionDate(date3)
                .exercises(List.of(
                        PerformedExercise.builder().exerciseTitle("Pushups").result(30.0).unit(MeasurementUnit.COUNT).greaterIsBetter(true).build()
                ))
                .build();

        final TestExecution test1 = TestExecution.builder()
                .athleteId(athleteId)
                .executionDate(date1)
                .exercises(List.of(
                        PerformedExercise.builder().exerciseTitle("Pushups").result(10.0).unit(MeasurementUnit.COUNT).greaterIsBetter(true).build(),
                        PerformedExercise.builder().exerciseTitle("Plank").result(50.0).unit(MeasurementUnit.SEC).greaterIsBetter(true).build()
                ))
                .build();

        final TestExecution test2 = TestExecution.builder()
                .athleteId(athleteId)
                .executionDate(date2)
                .exercises(List.of(
                        PerformedExercise.builder().exerciseTitle("Pushups").result(20.0).unit(MeasurementUnit.COUNT).greaterIsBetter(true).build(),
                        PerformedExercise.builder().exerciseTitle("Plank").result(60.0).unit(MeasurementUnit.SEC).greaterIsBetter(true).build()
                ))
                .build();

        // When
        final TestTrendReport trendReport = ReportCalculator.calculateTrend(athleteId, List.of(test3, test1, test2));

        // Then
        assertThat(trendReport.getAthleteId()).isEqualTo(athleteId);
        assertThat(trendReport.getTrends()).hasSize(2);

        // Pushups trend: should have 3 data points sorted by date (test1 -> test2 -> test3)
        final ExerciseTrend pushupsTrend = trendReport.getTrends().stream()
                .filter(t -> t.getExerciseTitle().equals("Pushups"))
                .findFirst().orElseThrow();
        assertThat(pushupsTrend.getDataPoints()).hasSize(3);
        assertThat(pushupsTrend.getDataPoints().get(0).getDate()).isEqualTo(date1);
        assertThat(pushupsTrend.getDataPoints().get(0).getResult()).isEqualTo(10.0);
        assertThat(pushupsTrend.getDataPoints().get(1).getDate()).isEqualTo(date2);
        assertThat(pushupsTrend.getDataPoints().get(1).getResult()).isEqualTo(20.0);
        assertThat(pushupsTrend.getDataPoints().get(2).getDate()).isEqualTo(date3);
        assertThat(pushupsTrend.getDataPoints().get(2).getResult()).isEqualTo(30.0);

        // Plank trend: should have 2 data points sorted by date (test1 -> test2)
        final ExerciseTrend plankTrend = trendReport.getTrends().stream()
                .filter(t -> t.getExerciseTitle().equals("Plank"))
                .findFirst().orElseThrow();
        assertThat(plankTrend.getDataPoints()).hasSize(2);
        assertThat(plankTrend.getDataPoints().get(0).getDate()).isEqualTo(date1);
        assertThat(plankTrend.getDataPoints().get(1).getDate()).isEqualTo(date2);
    }

    @Test
    void shouldTreatExercisesAsDifferentIfFlagsOrUnitsDiffer() {
        // Given
        final String athleteId = "athlete-123";
        final TestExecution testA = TestExecution.builder()
                .id("test-A")
                .athleteId(athleteId)
                .exercises(List.of(
                        PerformedExercise.builder()
                                .exerciseTitle("Pushups")
                                .result(10.0)
                                .unit(MeasurementUnit.COUNT)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();

        final TestExecution testB = TestExecution.builder()
                .id("test-B")
                .athleteId(athleteId)
                .exercises(List.of(
                        PerformedExercise.builder()
                                .exerciseTitle("Pushups")
                                .result(20.0)
                                .unit(MeasurementUnit.COUNT)
                                .greaterIsBetter(false) // opposite flag!
                                .build(),
                        PerformedExercise.builder()
                                .exerciseTitle("Pushups")
                                .result(30.0)
                                .unit(MeasurementUnit.KG) // different unit!
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();

        // When
        final TestComparisonReport report = ReportCalculator.compare(testA, testB);

        // Then
        // There should be 3 separate exercise results:
        // 1. Pushups | COUNT | true (Present in Test A only)
        // 2. Pushups | COUNT | false (Present in Test B only)
        // 3. Pushups | KG | true (Present in Test B only)
        assertThat(report.getComparisons()).hasSize(3);

        final ExerciseComparison ex1 = report.getComparisons().stream()
                .filter(c -> c.getExerciseTitle().equals("Pushups") && c.getUnit() == MeasurementUnit.COUNT && c.getGreaterIsBetter())
                .findFirst().orElseThrow();
        assertThat(ex1.getResultA()).isEqualTo(10.0);
        assertThat(ex1.getResultB()).isNull();
        assertThat(ex1.getDelta()).isNull();

        final ExerciseComparison ex2 = report.getComparisons().stream()
                .filter(c -> c.getExerciseTitle().equals("Pushups") && c.getUnit() == MeasurementUnit.COUNT && !c.getGreaterIsBetter())
                .findFirst().orElseThrow();
        assertThat(ex2.getResultA()).isNull();
        assertThat(ex2.getResultB()).isEqualTo(20.0);

        final ExerciseComparison ex3 = report.getComparisons().stream()
                .filter(c -> c.getExerciseTitle().equals("Pushups") && c.getUnit() == MeasurementUnit.KG)
                .findFirst().orElseThrow();
        assertThat(ex3.getResultA()).isNull();
        assertThat(ex3.getResultB()).isEqualTo(30.0);
    }
}
