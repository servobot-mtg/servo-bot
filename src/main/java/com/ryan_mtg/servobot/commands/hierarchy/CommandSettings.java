package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.commands.Permission;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommandSettings {
    private int flags;
    private Permission permission;
    private RateLimit rateLimit;

    public CommandSettings(final int flags, final Permission permission, final RateLimit rateLimit) {
        this.flags = flags;
        this.permission = permission;
        this.rateLimit = rateLimit;
    }
}
