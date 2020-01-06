package com.ryan_mtg.servobot.model.reaction;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

public class WatershedFilter extends AbstractReactionFilter {
    public static final int TYPE = 2;

    private String timeZone;

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public Pattern createPattern(final String patternString) {
        String curseWord = patternString;

        if (curseWord.charAt(0) == '!') {
            curseWord = curseWord.substring(1);
        }

        return super.createPattern(curseWord);
    }

    @Override
    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public boolean shouldReact() {
        return withinWaterShed();
    }

    private boolean withinWaterShed() {
        int hour = ZonedDateTime.now(ZoneId.of(timeZone)).getHour();
        return 6 <= hour && hour < 21;
    }
}
