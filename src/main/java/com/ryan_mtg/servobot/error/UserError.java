package com.ryan_mtg.servobot.error;

public class UserError extends Exception {
    public UserError(final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
    }

    public UserError(final String format, final Object... args) {
        super(String.format(format, args));
    }

    public static void filter(final ThrowingRunnable function) throws UserError {
        try {
            function.run();
        } catch (Exception e) {
            throw new UserError(e, e.getMessage());
        }
    }

    public static <ReturnType> ReturnType filter(final ThrowingFunction<ReturnType> function) throws UserError {
        try {
            return function.apply();
        } catch (Exception e) {
            throw new UserError(e, e.getMessage());
        }
    }
}
