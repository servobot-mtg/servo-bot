package com.ryan_mtg.servobot.utility;

import java.util.List;

public class Strings {
    public static String join(final List<String> strings) {
        StringBuilder builder = new StringBuilder();
        builder.append(strings.get(0));
        if (strings.size() == 2) {
            builder.append(" and ").append(strings.get(1));
            return builder.toString();
        }

        for (int i = 1; i < strings.size(); i++) {
            builder.append(", ");
            if (i + 1 == strings.size()) {
                builder.append("and ");
            }
            builder.append(strings.get(i));
        }

        return builder.toString();
    }

    public static boolean isBlank(final String string) {
        return string == null || string.isEmpty();
    }

    public static String trim(final String string) {
        if (string == null) {
            return string;
        }
        return string.trim();
    }

    public static Object capitalize(final String string) {
        if (isBlank(string) || Character.isUpperCase(string.charAt(0))) {
            return string;
        }
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
}
