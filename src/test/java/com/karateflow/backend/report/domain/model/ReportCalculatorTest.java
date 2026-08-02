package com.karateflow.backend.report.domain.model;

import com.karateflow.backend.test.domain.model.MeasurementUnit;
import com.karateflow.backend.test.domain.model.PerformedExercise;
import com.karateflow.backend.test.domain.model.TestExecution;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Suite di test unitari per {@link ReportCalculator}.
 * Documenta la validazione delle logiche di dominio (calcolo di percentuali, overlap) 
 * assicurando che le mutazioni matematiche o condizionali falliscano.
 */
class ReportCalculatorTest {

    /**
     * Happy path: Verifica che il confronto tra due test avvenga correttamente,
     * calcolando i delta, la variazione percentuale e l'overlap degli esercizi.
     */
    @Test
    void shouldCompareTestsSuccessfully() {
        // Arrange
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
                .executionDate(LocalDate.now().minusDays(10))
                .exercises(List.of(ex1A, ex2A))
                .build();

        final TestExecution testB = TestExecution.builder()
                .id("test-B")
                .athleteId(athleteId)
                .executionDate(LocalDate.now())
                .exercises(List.of(ex1B, ex3B))
                .build();

        // Act
        final TestComparisonReport report = ReportCalculator.compare(testA, testB);

        // Assert
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

    /**
     * Sad path / Edge case: Verifica il caso di overlap percentuale nullo o molto basso (< 30%).
     * Uccide i mutanti legati alle condizioni limite (ConditionalsBoundaryMutator).
     */
    @Test
    void shouldFlagLowOverlap() {
        // Arrange
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

        // Act
        final TestComparisonReport report = ReportCalculator.compare(testA, testB);

        // Assert
        // 0 shared, 5 total. Overlap is 0.0% -> lowOverlap should be true.
        assertThat(report.getOverlapPercentage()).isEqualTo(0.0);
        assertThat(report.isLowOverlap()).isTrue();
    }

    /**
     * Sad path: Verifica che venga lanciata un'eccezione se si confrontano esecuzioni di atleti differenti.
     * Difende le precondizioni da RemoveConditionalMutator.
     */
    @Test
    void shouldThrowExceptionWhenComparingDifferentAthletes() {
        // Arrange
        final TestExecution testA = TestExecution.builder().id("A").athleteId("athlete-1").build();
        final TestExecution testB = TestExecution.builder().id("B").athleteId("athlete-2").build();

        // Act/Then
        assertThatThrownBy(() -> ReportCalculator.compare(testA, testB))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot compare tests of different athletes");
    }

    /**
     * Sad path: Gestione della divisione per zero quando il risultato del primo test è 0.
     * Assicura che la percentuale restituita sia 0 per evitare NaN/Infinity, proteggendo la logica matematica.
     */
    @Test
    void shouldHandleDivisionByZeroInPercentage() {
        // Arrange
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

        // Act
        final TestComparisonReport report = ReportCalculator.compare(testA, testB);

        // Assert
        final ExerciseComparison comp = report.getComparisons().get(0);
        assertThat(comp.getDelta()).isEqualTo(10.0);
        assertThat(comp.getPercentageChange()).isEqualTo(0.0);
    }

    /**
     * Happy path: Verifica che i trend vengano calcolati raggruppando gli esercizi
     * in ordine cronologico.
     */
    @Test
    void shouldCalculateTrendsChronologically() {
        // Arrange
        final String athleteId = "athlete-123";
        final LocalDate date1 = LocalDate.now().minusDays(10);
        final LocalDate date2 = LocalDate.now().minusDays(5);
        final LocalDate date3 = LocalDate.now();

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

        // Act
        final TestTrendReport trendReport = ReportCalculator.calculateTrend(athleteId, List.of(test3, test1, test2));

        // Assert
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

    /**
     * Edge case: Esercizi con stesso nome ma unità di misura o flag "greaterIsBetter" differenti
     * devono essere considerati come esercizi distinti nel report.
     */
    @Test
    void shouldTreatExercisesAsDifferentIfFlagsOrUnitsDiffer() {
        // Arrange
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

        // Act
        final TestComparisonReport report = ReportCalculator.compare(testA, testB);

        // Assert
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

    /**
     * Sad path: Esecuzioni di test con lista di esercizi nulla.
     * Uccide i NullReturnValsMutator e assicura che il sistema gestisca le collection vuote o nulle.
     */
    @Test
    void shouldHandleNullExercisesInCompare() {
        // Arrange
        final TestExecution testA = TestExecution.builder()
                .id("test-A")
                .athleteId("athlete-123")
                .exercises(null)
                .build();
        final TestExecution testB = TestExecution.builder()
                .id("test-B")
                .athleteId("athlete-123")
                .exercises(null)
                .build();

        // Act
        final TestComparisonReport report = ReportCalculator.compare(testA, testB);

        // Assert
        assertThat(report.getComparisons()).isEmpty();
        assertThat(report.getOverlapPercentage()).isEqualTo(0.0);
        assertThat(report.isLowOverlap()).isTrue();
    }

    /**
     * Edge case: Overlap esattamente sulla soglia limite (30.0%).
     * Verifica il perimetro delle disuguaglianze per sconfiggere i ConditionalsBoundaryMutator.
     */
    @Test
    void shouldNotFlagLowOverlapWhenExactlyThreshold() {
        // Arrange
        // We want overlap percentage = 30.0% exactly.
        // If shared is 3 and total is 10, then 3/10 = 30%.
        // testA has 3 shared, 7 unique = 10. testB has 3 shared, 0 unique = 3.
        // Wait, total = union. 3 shared + 7 unique A = 10 total.
        final String athleteId = "athlete-123";
        final List<PerformedExercise> exercisesA = new java.util.ArrayList<>();
        final List<PerformedExercise> exercisesB = new java.util.ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            PerformedExercise ex = PerformedExercise.builder().exerciseTitle("Shared" + i).result(10.0).build();
            exercisesA.add(ex);
            exercisesB.add(ex);
        }
        for (int i = 0; i < 7; i++) {
            PerformedExercise ex = PerformedExercise.builder().exerciseTitle("Unique" + i).result(10.0).build();
            exercisesA.add(ex);
        }

        final TestExecution testA = TestExecution.builder().id("A").athleteId(athleteId).exercises(exercisesA).build();
        final TestExecution testB = TestExecution.builder().id("B").athleteId(athleteId).exercises(exercisesB).build();

        // Act
        final TestComparisonReport report = ReportCalculator.compare(testA, testB);

        // Assert
        assertThat(report.getOverlapPercentage()).isEqualTo(30.0);
        assertThat(report.isLowOverlap()).isFalse(); // Since it checks < 30.0
    }

    /**
     * Sad path: Verifica il calcolo del trend scartando atleti non inerenti e tollerando esecuzioni con esercizi a null.
     */
    @Test
    void shouldFilterOutOtherAthletesAndHandleNullExercisesInTrend() {
        // Arrange
        final String athleteId = "athlete-123";
        
        final TestExecution test1 = TestExecution.builder()
                .id("1")
                .athleteId(athleteId)
                .executionDate(LocalDate.now())
                .exercises(null) // tests line 116
                .build();
                
        final TestExecution test2 = TestExecution.builder()
                .id("2")
                .athleteId("other-athlete")
                .executionDate(LocalDate.now())
                .exercises(List.of(PerformedExercise.builder().exerciseTitle("Ex").result(10.0).build())) // tests line 108
                .build();

        // Act
        final TestTrendReport report = ReportCalculator.calculateTrend(athleteId, List.of(test1, test2));

        // Assert
        assertThat(report.getTrends()).isEmpty();
    }
}
