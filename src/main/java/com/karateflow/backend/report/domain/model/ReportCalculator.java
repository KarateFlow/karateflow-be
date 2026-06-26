package com.karateflow.backend.report.domain.model;

import com.karateflow.backend.test.domain.model.PerformedExercise;
import com.karateflow.backend.test.domain.model.TestExecution;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"PMD.UseConcurrentHashMap", "PMD.AvoidInstantiatingObjectsInLoops"})
public final class ReportCalculator {

    private static final double ZERO = 0.0;
    private static final double PERCENTAGE_FACTOR = 100.0;
    private static final int COMPARISON_EQUAL = 0;

    private ReportCalculator() {
        // Utility class
    }

    private static String getExerciseKey(final PerformedExercise exercise) {
        if (exercise == null) {
            return "";
        }
        return exercise.getExerciseTitle() + "|" + exercise.getUnit() + "|" + exercise.getGreaterIsBetter();
    }

    public static TestComparisonReport compare(final TestExecution testA, final TestExecution testB) {
        if (!testA.getAthleteId().equals(testB.getAthleteId())) {
            throw new IllegalArgumentException("Cannot compare tests of different athletes");
        }

        final List<PerformedExercise> exercisesA = testA.getExercises() != null ? testA.getExercises() : Collections.emptyList();
        final List<PerformedExercise> exercisesB = testB.getExercises() != null ? testB.getExercises() : Collections.emptyList();

        final Map<String, PerformedExercise> mapA = exercisesA.stream()
                .collect(Collectors.toMap(ReportCalculator::getExerciseKey, e -> e, (e1, e2) -> e1));
        final Map<String, PerformedExercise> mapB = exercisesB.stream()
                .collect(Collectors.toMap(ReportCalculator::getExerciseKey, e -> e, (e1, e2) -> e1));

        final Set<String> allKeys = new LinkedHashSet<>();
        exercisesA.forEach(e -> allKeys.add(getExerciseKey(e)));
        exercisesB.forEach(e -> allKeys.add(getExerciseKey(e)));

        final Set<String> sharedKeys = new HashSet<>(mapA.keySet());
        sharedKeys.retainAll(mapB.keySet());

        double overlapPercentage = ZERO;
        if (!allKeys.isEmpty()) {
            overlapPercentage = (double) sharedKeys.size() / allKeys.size() * PERCENTAGE_FACTOR;
        }
        final double overlapThreshold = 30.0;
        final boolean lowOverlap = overlapPercentage < overlapThreshold;

        final List<ExerciseComparison> comparisons = new ArrayList<>();
        for (final String key : allKeys) {
            final PerformedExercise exA = mapA.get(key);
            final PerformedExercise exB = mapB.get(key);

            final Double valA = exA != null ? exA.getResult() : null;
            final Double valB = exB != null ? exB.getResult() : null;

            Double delta = null;
            Double pctChange = null;

            if (valA != null && valB != null) {
                delta = valB - valA;
                if (Double.compare(valA, ZERO) == COMPARISON_EQUAL) {
                    pctChange = ZERO;
                } else {
                    pctChange = delta / valA * PERCENTAGE_FACTOR;
                }
            }

            final PerformedExercise exerciseRef = exB != null ? exB : exA;
            if (exerciseRef == null) {
                continue;
            }

            comparisons.add(ExerciseComparison.builder()
                    .exerciseTitle(exerciseRef.getExerciseTitle())
                    .resultA(valA)
                    .resultB(valB)
                    .delta(delta)
                    .percentageChange(pctChange)
                    .unit(exerciseRef.getUnit())
                    .greaterIsBetter(exerciseRef.getGreaterIsBetter())
                    .build());
        }

        return TestComparisonReport.builder()
                .athleteId(testA.getAthleteId())
                .testIdA(testA.getId())
                .testIdB(testB.getId())
                .overlapPercentage(overlapPercentage)
                .lowOverlap(lowOverlap)
                .comparisons(comparisons)
                .build();
    }

    public static TestTrendReport calculateTrend(final String athleteId, final List<TestExecution> tests) {
        // Ensure tests belong to athlete and are sorted chronologically
        final List<TestExecution> sortedTests = tests.stream()
                .filter(t -> t.getAthleteId().equals(athleteId))
                .sorted(Comparator.comparing(TestExecution::getExecutionDate))
                .collect(Collectors.toList());

        final Map<String, List<TrendDataPoint>> trendMap = new LinkedHashMap<>();
        final Map<String, PerformedExercise> metaMap = new HashMap<>();

        for (final TestExecution test : sortedTests) {
            if (test.getExercises() == null) {
                continue;
            }
            for (final PerformedExercise exercise : test.getExercises()) {
                final String key = getExerciseKey(exercise);
                trendMap.computeIfAbsent(key, keyParam -> new ArrayList<>())
                        .add(new TrendDataPoint(test.getExecutionDate(), exercise.getResult()));
                metaMap.putIfAbsent(key, exercise);
            }
        }

        final List<ExerciseTrend> trends = trendMap.entrySet().stream()
                .map(entry -> {
                    final String key = entry.getKey();
                    final PerformedExercise meta = metaMap.get(key);
                    return ExerciseTrend.builder()
                            .exerciseTitle(meta.getExerciseTitle())
                            .unit(meta.getUnit())
                            .greaterIsBetter(meta.getGreaterIsBetter())
                            .dataPoints(entry.getValue())
                            .build();
                })
                .collect(Collectors.toList());

        return TestTrendReport.builder()
                .athleteId(athleteId)
                .trends(trends)
                .build();
    }
}
