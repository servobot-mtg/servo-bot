package com.ryan_mtg.servobot.model.parser;

public class ParseException extends Exception {
    private String errorMessage;

    public ParseException(final String errorMessage) {
        super(errorMessage);
    }

    public ParseException(final String errorMessage, final Throwable cause) {
        super(errorMessage, cause);
    }
}
