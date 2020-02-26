package com.ryan_mtg.servobot.commands;

import java.time.Duration;

public class CommandSettings {
    private int flags;
    private Permission permission;
    private Duration rateLimitDuration;

    public CommandSettings(final int flags, final Permission permission, final Duration rateLimitDuration) {
        this.flags = flags;
        this.permission = permission;
        this.rateLimitDuration = rateLimitDuration;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(final int flags) {
        this.flags = flags;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(final Permission permission) {
        this.permission = permission;
    }

    public Duration getRateLimitDuration() {
        return rateLimitDuration;
    }

    public void setRateLimitDuration(final Duration rateLimitDuration) {
        this.rateLimitDuration = rateLimitDuration;
    }
}
