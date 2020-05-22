package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import lombok.EqualsAndHashCode;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class RateLimiter {
    private Map<Access, Instant> lastUserAccess = new HashMap<>();
    private Map<Integer, Instant> lastGlobalAccess = new HashMap<>();

    private Clock clock;

    public RateLimiter() {
        this(Clock.systemUTC());
    }

    public RateLimiter(final Clock clock) {
        this.clock = clock;
    }

    public boolean allow(final int userId, final int commandId, final RateLimit rateLimit) {
        if (rateLimit.allowAll()) {
            return true;
        }

        Instant now = clock.instant();

        Duration globalDuration = rateLimit.getGlobalDuration();
        if (globalDuration != null) {
            if (lastGlobalAccess.containsKey(commandId)) {
                Instant previousAccess = lastGlobalAccess.get(commandId);
                if (now.compareTo(previousAccess.plus(globalDuration)) < 0) {
                    return false;
                }
            }
            lastGlobalAccess.put(commandId, now);
        }

        /*
        Access access = new Access(userId, commandId);
        if (lastAccess.containsKey(access)) {
            Instant previousAccess = lastAccess.get(access);
            if (0 < previousAccess.plus(rateLimitDuration).compareTo(now)) {
                return false;
            }
        }
        lastAccess.put(access, now);
         */
        return true;
    }

    @EqualsAndHashCode
    private static class Access {
        private final int userId;
        private final int commandId;

        public Access(final int userId, final int commandId) {
            this.userId = userId;
            this.commandId = commandId;
        }
    }
}
