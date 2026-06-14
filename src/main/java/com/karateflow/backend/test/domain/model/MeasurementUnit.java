package com.karateflow.backend.test.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("PMD.ShortVariable")
public enum MeasurementUnit {
    KG("kg"),
    SEC("sec"),
    CM("cm"),
    COUNT("n°");

    private final String symbol;
}
