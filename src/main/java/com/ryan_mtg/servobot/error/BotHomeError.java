package com.ryan_mtg.servobot.error;

public class BotHomeError extends Exception {
    public BotHomeError(final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
    }

    public BotHomeError(final String format, final Object... args) {
        super(String.format(format, args));
    }

    public static <ReturnType> ReturnType filter(final ThrowingFunction<ReturnType> function) throws BotHomeError {
        try {
            return function.apply();
        } catch (Exception e) {
            throw new BotHomeError(e, e.getMessage());
        }
    }
}
