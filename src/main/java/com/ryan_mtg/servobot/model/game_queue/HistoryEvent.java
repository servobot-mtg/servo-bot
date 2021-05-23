package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Time;

import java.time.Instant;

public class HistoryEvent {
    private Instant when;
    private HomedUser who;
    private String what;

    public HistoryEvent(final HomedUser who, final String what) {
        this.when = Instant.now();
        this.who = who;
        this.what = what;
    }

    public HistoryEvent(final String what) {
        this(null, what);
    }

    public void append(final StringBuilder message, final String timeZone) {
        message.append(Time.toReadableString(when, timeZone)).append(": ");
        if (who != null) {
            message.append(who.getName());
        }
        message.append(what);
    }
}
