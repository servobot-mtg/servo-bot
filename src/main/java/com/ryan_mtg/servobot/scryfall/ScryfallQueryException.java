package com.ryan_mtg.servobot.scryfall;

import lombok.Getter;

@Getter
public class ScryfallQueryException extends RuntimeException {
    private final String details;

    public ScryfallQueryException(final String details) {
        this.details = details;
    }
}
