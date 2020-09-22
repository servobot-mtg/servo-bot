package com.ryan_mtg.servobot.utility;

import com.ryan_mtg.servobot.error.SystemError;

public class Assertion {
    public static void assertNotNull(final Object object, final String name) throws SystemError {
        if (object == null) {
            throw new SystemError("%s is null", name);
        }
    }
}
