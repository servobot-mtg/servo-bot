package com.ryan_mtg.servobot.error;

public class LibraryError extends Exception {
    public LibraryError(final String format, final Object... args) {
        super(String.format(format, args));
    }
}
