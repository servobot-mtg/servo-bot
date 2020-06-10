package com.ryan_mtg.servobot.model.alerts;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.utility.Time;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

public class ContinualGenerator extends AlertGenerator {
    public static final int TYPE = 1;

    @Getter
    private Duration duration;

    private Instant goal;

    public ContinualGenerator(final int id, final String alertToken, final Duration duration) throws UserError {
        super(id, alertToken);

        this.duration = duration;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getDescription() {
        return String.format("Alert every %s", Time.toReadableString(duration));
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
