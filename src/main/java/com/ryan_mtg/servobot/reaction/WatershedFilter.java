package com.ryan_mtg.servobot.reaction;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

public class WatershedFilter extends AbstractReactionFilter {
    public static final int TYPE = 2;

    @Override
    public Pattern createPattern(final String patternString) {
        String curseWord = patternString;

        if (curseWord.charAt(0) == '!') {
            curseWord = curseWord.substring(1);
        }

        return super.createPattern(curseWord);
    }

    @Override
    public boolean shouldReact() {
        return withinWaterShed();
    }

    @Override
    public int getType() {
        return TYPE;
    }

    private static boolean withinWaterShed() {
        int hour = ZonedDateTime.now(ZoneId.of("America/Vancouver")).getHour();
        return 6 <= hour && hour < 21;
    }
}
