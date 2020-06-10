package com.ryan_mtg.servobot.error;

public class SystemBadError {
    public SystemBadError(final Throwable cause, final String format, final Object... args) {
        //super(String.format(format, args), cause);
    }

    public SystemBadError(final String format, final Object... args) {
        //super(String.format(format, args));
    }

    public interface ThrowingFunction <ReturnType> {
        ReturnType apply() throws Exception;
    }
}
