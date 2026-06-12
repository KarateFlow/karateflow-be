package com.karateflow.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AthleteAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AthleteAlreadyExistsException(final String firstName, final String lastName) {
        super(String.format("Athlete %s %s already exists in the system", firstName, lastName));
    }
}
