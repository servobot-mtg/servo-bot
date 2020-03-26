package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.utility.Flags;
import lombok.Getter;
import lombok.Setter;

public class GiveawayCommandSettings {
    @Getter @Setter
    private String commandName;

    @Getter @Setter
    private int flags;

    @Getter @Setter
    private Permission permission;

    @Getter @Setter
    private String message;

    public GiveawayCommandSettings(final String commandName, final int flags, final Permission permission,
                                   final String message) {
        this.commandName = commandName;
        this.flags = flags;
        this.permission = permission;
        this.message = message;
    }

    public boolean isSecure() {
        return Flags.hasFlag(flags, Command.SECURE_FLAG);
    }

    public boolean isDiscord() {
        return Flags.hasFlag(flags, Command.DISCORD_FLAG);
    }

    public boolean isTwitch() {
        return Flags.hasFlag(flags, Command.TWITCH_FLAG);
    }
}
