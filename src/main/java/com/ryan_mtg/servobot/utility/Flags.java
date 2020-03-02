package com.ryan_mtg.servobot.utility;

public class Flags {
    public static boolean hasFlag(final int flags, final int flag) {
        return (flags & flag) != 0;
    }

    public static int setFlag(final int flags, final int flag, final boolean value) {
        if (value) {
            return flags | flag;
        } else {
            return flags & ~flag;
        }
    }
}
