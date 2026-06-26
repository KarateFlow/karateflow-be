package com.karateflow.backend.common.exception;

import java.io.Serial;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestTemplateNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public TestTemplateNotFoundException(final String message) {
        super(message);
    }
}
