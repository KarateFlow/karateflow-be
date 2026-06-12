package com.karateflow.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AthleteAlreadyExistsException extends RuntimeException {
    public AthleteAlreadyExistsException(String firstName, String lastName) {
        super(String.format("Athlete %s %s already exists in the system", firstName, lastName));
    }
}
