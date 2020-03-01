package com.ryan_mtg.servobot.model.alerts;

import com.ryan_mtg.servobot.events.BotErrorException;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;

public class ContinualGenerator extends AlertGenerator {
    public static final int TYPE = 1;

    private Duration duration;
    private Instant goal;

    public ContinualGenerator(final int id, final String alertToken, final Duration duration) throws BotErrorException {
        super(id, alertToken);

        this.duration = duration;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getDescription() {
        return String.format("Alert every %s", DurationFormatUtils.formatDuration(duration.toMillis(), "H:mm:ss"));
    }

    @Override
    public void setTimeZone(final String timeZone) {}

    @Override
    public Instant getNextAlertTime(final Instant now) {
        if (goal == null) {
            goal = now.plus(duration);
        }

        while (goal.isBefore(now)) {
            goal = goal.plus(duration);
        }

        return goal;
    }
}
