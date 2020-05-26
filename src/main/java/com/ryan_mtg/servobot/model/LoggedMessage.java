package com.ryan_mtg.servobot.model;

import lombok.Getter;

import java.time.Instant;
import com.ryan_mtg.servobot.user.User;

@Getter
public class LoggedMessage {
    public static final int TO_BOT = 1;
    public static final int FROM_BOT = 2;

    private final User user;
    private final int direction;
    private final String message;
    private final int serviceType;
    private final Instant sentTime;


    public LoggedMessage(final User user, final int direction, final String message, final int serviceType,
            final Instant sentTime) {
        this.user = user;
        this.direction = direction;
        this.message = message;
        this.serviceType = serviceType;
        this.sentTime = sentTime;
    }
}
