package com.karateflow.backend.common.handler;

import com.karateflow.backend.common.exception.AthleteAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DuplicateKeyException;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@lombok.extern.slf4j.Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({AthleteAlreadyExistsException.class, DuplicateKeyException.class})
    public ProblemDetail handleAthleteAlreadyExistsException(final Exception exception) {
        if (log.isWarnEnabled()) {
            log.warn("Athlete already exists conflict: {}", exception.getMessage());
        }
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Athlete already exists in the system");
        problemDetail.setTitle("Athlete Conflict");
        problemDetail.setType(URI.create("https://karateflow.com/errors/athlete-already-exists"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(final MethodArgumentNotValidException exception) {
        if (log.isWarnEnabled()) {
            log.warn("Validation failed for request: {}", exception.getBindingResult().getAllErrors());
        }
        final String detail = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (a, b) -> a + "; " + b);
        
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed: " + detail);
        problemDetail.setTitle("Invalid Request Content");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(final Exception exception) {
        if (log.isErrorEnabled()) {
            log.error("Unexpected error occurred", exception);
        }
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
