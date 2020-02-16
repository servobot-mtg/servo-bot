package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.twitch.model.TwitchService;

public abstract class Command {
    public static final int UNREGISTERED_ID = 0;

    public static final int SECURE_FLAG = 1<<0;
    public static final int TWITCH_FLAG = 1<<TwitchService.TYPE;
    public static final int DISCORD_FLAG = 1<<DiscordService.TYPE;
    public static final int DEFAULT_FLAGS = TWITCH_FLAG | DISCORD_FLAG;

    private int id;
    private int flags;
    private Permission permission;

    public Command(final int id, final int flags, final Permission permission) {
        this.id = id;
        this.flags = flags;
        this.permission = permission;
    }

    public final int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getFlags() {
        return flags;
    }

    public boolean isSecure() {
        return (flags & SECURE_FLAG) != 0;
    }

    public boolean isTwitch() {
        return (flags & TWITCH_FLAG) != 0;
    }

    public boolean isDiscord() {
        return (flags & DISCORD_FLAG) != 0;
    }

    public void setSecure(final boolean secure) {
        setFlag(SECURE_FLAG, secure);
    }

    public boolean getService(int serivceType) {
        return (flags & 1<<serivceType) != 0;
    }

    public void setService(int serivceType, boolean value) {
        setFlag(1<<serivceType, value);
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(final Permission permission) {
        this.permission = permission;
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

    private void setFlag(final int flag, final boolean value) {
        if (value) {
            flags = flags | flag;
        } else {
            flags = flags & ~flag;
        }
    }
}
