package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.utility.Flags;
import lombok.Getter;
import lombok.Setter;

public abstract class Command {
    public static final int UNREGISTERED_ID = 0;

    public static final int SECURE_FLAG = 1 << 0;
    public static final int TWITCH_FLAG = 1 << TwitchService.TYPE;
    public static final int DISCORD_FLAG = 1 << DiscordService.TYPE;
    public static final int DEFAULT_FLAGS = TWITCH_FLAG | DISCORD_FLAG;
    public static final int ONLY_WHILE_STREAMING_FLAG = 1 << 5;
    public static final int TEMPORARY_FLAG = 1 << 10;

    @Getter @Setter
    private int id;

    private final CommandSettings commandSettings;

    public Command(final int id, final CommandSettings commandSettings) {
        this.id = id;
        this.commandSettings = commandSettings;
    }

    public int getFlags() {
        return commandSettings.getFlags();
    }

    public boolean isSecure() {
        return Flags.hasFlag(commandSettings.getFlags(), SECURE_FLAG);
    }

    public boolean isTwitch() {
        return Flags.hasFlag(commandSettings.getFlags(), TWITCH_FLAG);
    }

    public boolean isDiscord() {
        return Flags.hasFlag(commandSettings.getFlags(), DISCORD_FLAG);
    }

    public void setSecure(final boolean isSecure) {
        setFlag(SECURE_FLAG, isSecure);
    }

    public boolean getService(final int serviceType) {
        return Flags.hasFlag(commandSettings.getFlags(), 1<<serviceType);
    }

    public void setService(final int serviceType, final boolean value) {
        setFlag(1<<serviceType, value);
    }

    public boolean isTemporary() {
        return Flags.hasFlag(commandSettings.getFlags(), TEMPORARY_FLAG);
    }

    public void setTemporary() {
        setFlag(TEMPORARY_FLAG, true);
    }

    public boolean isOnlyWhileStreaming() {
        return Flags.hasFlag(commandSettings.getFlags(), ONLY_WHILE_STREAMING_FLAG);
    }

    public void setOnlyWhileStreaming(final boolean isOnlyWhileStreaming) {
        setFlag(ONLY_WHILE_STREAMING_FLAG, isOnlyWhileStreaming);
    }

    public Permission getPermission() {
        return commandSettings.getPermission();
    }

    public void setPermission(final Permission permission) {
        this.commandSettings.setPermission(permission);
    }

    public RateLimit getRateLimit() {
        return commandSettings.getRateLimit();
    }

    public abstract CommandType getType();
    public abstract void acceptVisitor(CommandVisitor commandVisitor);

    public boolean hasPermissions(final com.ryan_mtg.servobot.model.User user) {
        return hasPermissions(user, getPermission());
    }

    public boolean hasPermissions(final User user) {
        return hasPermissions(user, getPermission());
    }

    public boolean hasPermissions(final HomedUser homedUser) {
        return hasPermissions(homedUser, getPermission());
    }

    public static boolean hasPermissions(final com.ryan_mtg.servobot.model.User user, final Permission permission) {
        if (user.getHomedUser() != null) {
            return hasPermissions(user.getHomedUser(), permission);
        }
        return hasPermissions(user.getUser(), permission);
    }

    public static boolean hasPermissions(final HomedUser user, final Permission permission) {
        switch (permission) {
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
                if (user.isStreamer()) {
                    return true;
                }
            case ADMIN:
                if (user.isAdmin()) {
                    return true;
                }
                break;
            default:
                throw new IllegalStateException("Unhandled permission: " + permission);
        }
        return false;
    }

    public static boolean hasPermissions(final User user, final Permission permission) {
        switch (permission) {
            case ANYONE:
                return true;
            case SUB:
            case MOD:
            case STREAMER:
                return false;
            case ADMIN:
                if (user.isAdmin()) {
                    return true;
                }
                break;
            default:
                throw new IllegalStateException("Unhandled permission: " + permission);
        }
        return false;
    }

    private void setFlag(final int flag, final boolean value) {
        commandSettings.setFlags(Flags.setFlag(commandSettings.getFlags(), flag, value));
    }
}
