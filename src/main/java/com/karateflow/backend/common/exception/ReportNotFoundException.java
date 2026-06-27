package com.karateflow.backend.common.exception;

import java.io.Serial;

public class ReportNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ReportNotFoundException(final String message) {
        super(message);
    }
}
