package com.karateflow.backend.report.domain.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class TrendDataPoint {
    private final LocalDate date;
    private final Double result;
}
