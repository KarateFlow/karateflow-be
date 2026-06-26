package com.karateflow.backend.report.domain.model;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TrendDataPoint {
    private final LocalDateTime date;
    private final Double result;
}
