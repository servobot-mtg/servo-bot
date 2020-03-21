package com.ryan_mtg.servobot.controllers.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccessDeniedException extends ResponseStatusException {
    public AccessDeniedException(final String reason) {
        super(HttpStatus.FORBIDDEN, reason);
    }
}
