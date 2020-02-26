package com.ryan_mtg.servobot.commands;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RateLimiter {
    private Map<Access, Instant> lastAccess = new HashMap<>();

    public boolean allow(final int userId, final int commandId, final Duration rateLimitDuration) {
        if (rateLimitDuration == null) {
            return true;
        }
        Access access = new Access(userId, commandId);
        Instant now = Instant.now();
        if (lastAccess.containsKey(access)) {
            Instant previousAccess = lastAccess.get(access);
            if (0 < previousAccess.plus(rateLimitDuration).compareTo(now)) {
                return false;
            }
        }
        lastAccess.put(access, now);
        return true;
    }

    private static class Access {
        private final int userId;
        private final int commandId;

        public Access(final int userId, final int commandId) {
            this.userId = userId;
            this.commandId = commandId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Access access = (Access) o;
            return userId == access.userId && commandId == access.commandId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, commandId);
        }
    }
}
