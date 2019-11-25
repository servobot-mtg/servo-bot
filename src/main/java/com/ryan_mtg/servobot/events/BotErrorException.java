package com.ryan_mtg.servobot.events;

public class BotErrorException extends Exception {
    private String errorMessage;

    public BotErrorException(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
