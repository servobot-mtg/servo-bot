package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.Event;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.parser.ParseException;
import com.ryan_mtg.servobot.model.parser.Parser;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.utility.Flags;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Command {
    public static final int UNREGISTERED_ID = 0;

    private static Pattern REPLACEMENT_PATTERN = Pattern.compile("%([^%]*)%");

    public static final int SECURE_FLAG = 1<<0;
    public static final int TWITCH_FLAG = 1<<TwitchService.TYPE;
    public static final int DISCORD_FLAG = 1<<DiscordService.TYPE;
    public static final int DEFAULT_FLAGS = TWITCH_FLAG | DISCORD_FLAG;
    public static final int ONLY_WHILE_STREAMING_FLAG = 1<<5;
    public static final int TEMPORARY_FLAG = 1<<10;

    @Getter @Setter
    private int id;

    @Getter
    private int flags;

    @Getter @Setter
    private Permission permission;

    public Command(final int id, final int flags, final Permission permission) {
        this.id = id;
        this.flags = flags;
        this.permission = permission;
    }

    public boolean isSecure() {
        return Flags.hasFlag(flags, SECURE_FLAG);
    }

    public boolean isTwitch() {
        return Flags.hasFlag(flags, TWITCH_FLAG);
    }

    public boolean isDiscord() {
        return Flags.hasFlag(flags, DISCORD_FLAG);
    }

    public void setSecure(final boolean isSecure) {
        setFlag(SECURE_FLAG, isSecure);
    }

    public boolean getService(final int serviceType) {
        return Flags.hasFlag(flags, 1<<serviceType);
    }

    public void setService(final int serviceType, final boolean value) {
        setFlag(1<<serviceType, value);
    }

    public boolean isTemporary() {
        return Flags.hasFlag(flags, TEMPORARY_FLAG);
    }

    public void setTemporary() {
        setFlag(TEMPORARY_FLAG, true);
    }

    public boolean isOnlyWhileStreaming() {
        return Flags.hasFlag(flags, ONLY_WHILE_STREAMING_FLAG);
    }

    public void setOnlyWhileStreaming(final boolean isOnlyWhileStreaming) {
        setFlag(ONLY_WHILE_STREAMING_FLAG, isOnlyWhileStreaming);
    }

    public abstract int getType();
    public abstract void acceptVisitor(CommandVisitor commandVisitor);

    public boolean hasPermissions(final User user) {
        return hasPermissions(user, getPermission());
    }

    public static boolean hasPermissions(final User user, final Permission permission) {
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
                if (user.getHomedUser().isStreamer()) {
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

    protected static void say(final Channel channel, final Event event, final Scope scope, final String text)
            throws BotErrorException {
        channel.say(evaluate(event, scope, text));
    }

    private void setFlag(final int flag, final boolean value) {
        flags = Flags.setFlag(flags, flag, value);
    }

    private static String evaluate(final Event event, final Scope scope, final String text) throws BotErrorException {
        StringBuilder result = new StringBuilder();
        Matcher matcher = REPLACEMENT_PATTERN.matcher(text);
        int currentIndex = 0;

        Parser parser = new Parser(scope, event.getHomeEditor());

        while (matcher.find()) {
            result.append(text, currentIndex, matcher.start());
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
