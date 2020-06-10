package com.ryan_mtg.servobot.error;

public class BotError extends Exception {
    public BotError(final String format, final Object... args) {
        super(String.format(format, args));
    }
}
