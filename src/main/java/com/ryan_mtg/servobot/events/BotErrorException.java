package com.ryan_mtg.servobot.events;

import lombok.Getter;

public class BotErrorException extends Exception {
    @Getter
    private String errorMessage;

    public BotErrorException(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
