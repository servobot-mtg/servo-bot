package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.Event;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.parser.ParseException;
import com.ryan_mtg.servobot.model.parser.Parser;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.twitch.model.TwitchService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Command {
    public static final int UNREGISTERED_ID = 0;

    private static Pattern REPLACEMENT_PATTERN = Pattern.compile("%([^%]*)%");

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

    protected static void say(final Channel channel, final Event event, final Scope scope, final String text)
            throws BotErrorException {
        channel.say(evaluate(event, scope, text));
    }

    private static String evaluate(final Event event, final Scope scope, final String text) throws BotErrorException {
        StringBuilder result = new StringBuilder();
        Matcher matcher = REPLACEMENT_PATTERN.matcher(text);
        int currentIndex = 0;

        Parser parser = new Parser(scope, event.getHomeEditor());

        while (matcher.find()) {
            result.append(text.substring(currentIndex, matcher.start()));
            String expression = matcher.group(1);

            try {
                result.append(parser.parse(expression).evaluate());
            } catch (ParseException e) {
                throw new BotErrorException(String.format("Failed to parse %%%s%%: %s", expression, e.getMessage()));
            }

            currentIndex = matcher.end();
        }

        result.append(text.substring(currentIndex));
        return result.toString();
    }
}
