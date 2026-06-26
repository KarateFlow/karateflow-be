package com.karateflow.backend.common.exception;

import java.io.Serial;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestExecutionNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public TestExecutionNotFoundException(final String message) {
        super(message);
    }
}
