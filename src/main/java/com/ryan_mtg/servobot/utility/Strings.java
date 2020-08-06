package com.ryan_mtg.servobot.utility;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            return null;
        }
        return string.trim();
    }

    public static Object capitalize(final String string) {
        if (isBlank(string) || Character.isUpperCase(string.charAt(0))) {
            return string;
        }
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static String replace(final String text, final Pattern pattern,
            final Function<Matcher, String> replaceFunction) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder message = new StringBuilder();
        int index = 0;
        while(matcher.find(index)) {
            int start = matcher.start();
            int end = matcher.end();

            String replacement = replaceFunction.apply(matcher);
            if (replacement == null) {
                message.append(text, index, end);
            } else {
                message.append(text, index, start);
                message.append(replacement);
            }

            index = end;
        }

        message.append(text.substring(index));
        return message.toString();
    }
}
