package com.ryan_mtg.servobot.error;

public class SystemError extends RuntimeException {
    public SystemError(final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
    }

    public SystemError(final String format, final Object... args) {
        super(String.format(format, args));
    }

    public static <ReturnType> ReturnType filter(final ThrowingFunction<ReturnType> function) {
        try {
            return function.apply();
        } catch (Exception e) {
            throw new SystemError(e, e.getMessage());
        }
    }
}
