package com.ryan_mtg.servobot.model.reaction;

import com.ryan_mtg.servobot.model.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class WatershedFilter implements ReactionFilter {
    public static final int TYPE = 2;

    private String timeZone;

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public boolean shouldReact(final User sender) {
        return withinWaterShed();
    }

    private boolean withinWaterShed() {
        int hour = ZonedDateTime.now(ZoneId.of(timeZone)).getHour();
        return 6 <= hour && hour < 21;
    }
}
