package com.ryan_mtg.servobot.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFoundException extends ResponseStatusException {
    public ResourceNotFoundException(final String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }
}
