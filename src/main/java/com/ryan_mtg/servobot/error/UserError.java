package com.ryan_mtg.servobot.error;

public class UserError extends Exception {
    public UserError(final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
    }

    public UserError(final String format, final Object... args) {
        super(String.format(format, args));
    }
}
