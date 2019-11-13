package com.ryan_mtg.servobot.model;

import java.time.Duration;
import java.time.Instant;

public class ContinualGenerator extends AlertGenerator {
    public static final int TYPE = 1;

    private Duration duration;
    private Instant goal;

    public ContinualGenerator(final int id, final String alertToken, final Duration duration) {
        super(id, alertToken);

        this.duration = duration;
    }

    @Override
    public int getType() {
        return TYPE;
    }

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
