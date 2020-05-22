package com.ryan_mtg.servobot.commands.hierarchy;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter @Setter
public class RateLimit {
    private Duration globalDuration;

    public boolean allowAll() {
        return globalDuration == null;
    }
}
