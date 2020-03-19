package com.ryan_mtg.servobot.utility;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Function;

public class Time {
    private static final UnitDescriptor[] unitDescriptors = {
        new UnitDescriptor("week", duration -> duration.toDays(), 7, "a"),
        new UnitDescriptor("day", duration -> duration.toHours(), 24, "a"),
        new UnitDescriptor("hour", duration -> duration.toMinutes(), 60, "an"),
        new UnitDescriptor("minute", duration -> duration.getSeconds(), 60, "a"),
        new UnitDescriptor("second", duration -> duration.getSeconds(), 1, "a"),
        new UnitDescriptor("", duration -> 0L, 1, ""),
    };
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

    public static String toReadableString(final Duration duration) {
        for (int i = 0; i + 1 < unitDescriptors.length; i++) {
            String result = toReadableHelper(duration, unitDescriptors[i], unitDescriptors[i + 1]);
            if (result != null) {
                return result;
            }
        }
        return "no time";
    }

    public static String toReadableString(final LocalTime localTime) {
        return FORMATTER.format(localTime);
    }

    public static String toReadableString(final ZonedDateTime goal) {
        return FORMATTER.format(goal);
    }

    private static class UnitDescriptor {
        @Getter
        private Function<Duration, Long> extractor;

        @Getter
        private int conversion;

        @Getter
        private String name;

        @Getter
        private String article;

        public UnitDescriptor(final String name, final Function<Duration, Long> extractor, final int conversion,
                              final String article) {
            this.name = name;
            this.extractor = extractor;
            this.conversion = conversion;
            this.article = article;
        }
    }

    private static String toReadableHelper(final Duration duration, final UnitDescriptor bigUnitDescriptor,
                                           final UnitDescriptor smallUnitDescriptor) {
        long bigUnit = bigUnitDescriptor.getExtractor().apply(duration);
        int smallUnitConversion = bigUnitDescriptor.getConversion();
        long roundedBigUnit = (bigUnit + smallUnitConversion/2) / smallUnitConversion;
        long flooredBigUnit = bigUnit / smallUnitConversion;

        if (roundedBigUnit > 1) {
            return String.format("%d %ss", roundedBigUnit, bigUnitDescriptor.getName());
        } else if (flooredBigUnit == 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(bigUnitDescriptor.getArticle()).append(' ').append(bigUnitDescriptor.getName());

            long smallUnit = smallUnitDescriptor.getExtractor().apply(duration);
            int smallerUnitConversion = smallUnitDescriptor.getConversion();
            long roundedSmallUnit = (smallUnit + smallerUnitConversion/2) / smallerUnitConversion - smallUnitConversion;
            if (roundedSmallUnit > 1) {
                sb.append(" and ").append(roundedSmallUnit).append(' ');
                sb.append(smallUnitDescriptor.getName()).append('s');
            } else if (roundedSmallUnit == 1) {
                sb.append(" and ").append(smallUnitDescriptor.getArticle());
                sb.append(' ').append(smallUnitDescriptor.getName());
            }
            return sb.toString();
        }
        return null;
    }
}
