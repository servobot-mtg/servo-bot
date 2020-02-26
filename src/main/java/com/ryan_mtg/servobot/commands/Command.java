package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.twitch.model.TwitchService;

import java.time.Duration;

public abstract class Command {
    public static final int UNREGISTERED_ID = 0;

    public static final int SECURE_FLAG = 1<<0;
    public static final int TWITCH_FLAG = 1<<TwitchService.TYPE;
    public static final int DISCORD_FLAG = 1<<DiscordService.TYPE;
    public static final int DEFAULT_FLAGS = TWITCH_FLAG | DISCORD_FLAG;

    private int id;
    CommandSettings commandSettings;

    public Command(final int id, final CommandSettings commandSettings) {
        this.id = id;
        this.commandSettings = commandSettings;
    }

    public final int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getFlags() {
        return commandSettings.getFlags();
    }

    public boolean isSecure() {
        return getFlag(SECURE_FLAG);
    }

    public boolean isTwitch() {
        return getFlag(TWITCH_FLAG);
    }

    public boolean isDiscord() {
        return getFlag(DISCORD_FLAG);
    }

    public void setSecure(final boolean secure) {
        setFlag(SECURE_FLAG, secure);
    }

    public boolean getService(final int serivceType) {
        return getFlag(1<<serivceType);
    }

    public void setService(final int serivceType, final boolean value) {
        setFlag(1<<serivceType, value);
    }

    public Permission getPermission() {
        return commandSettings.getPermission();
    }

    public void setPermission(final Permission permission) {
        this.commandSettings.setPermission(permission);
    }

    public Duration getRateLimitDuration() {
        return commandSettings.getRateLimitDuration();
    }

    public void setRateLimitDuration(final Duration rateLimitDuration) {
        this.commandSettings.setRateLimitDuration(rateLimitDuration);
    }

    public abstract int getType();
    public abstract void acceptVisitor(CommandVisitor commandVisitor);

    public boolean hasPermissions(final User user) {
        switch (getPermission()) {
            case ANYONE:
                return true;
            case SUB:
                if (user.isSubscriber()) {
                    return true;
                }
            case MOD:
                if (user.isModerator()) {
                    return true;
                }
            case STREAMER:
                if (user.getHomedUser().isStreamer()) {
                    return true;
                }
            case ADMIN:
                if (user.isAdmin()) {
                    return true;
                }
                break;
            default:
                throw new IllegalStateException("Unhandled permission: " + getPermission());
        }
        return false;
    }

    private boolean getFlag(final int flag) {
        return (getFlags() & flag) != 0;
    }

    private void setFlag(final int flag, final boolean value) {
        if (value) {
            commandSettings.setFlags(getFlags() | flag);
        } else {
            commandSettings.setFlags(getFlags() & ~flag);
        }
    }
}
