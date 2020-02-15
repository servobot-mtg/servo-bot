package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.discord.model.DiscordUser;
import com.ryan_mtg.servobot.discord.model.DiscordUserStatus;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.user.HomedUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;

public class DiscordEventAdapter extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordEventAdapter.class);
    private EventListener listener;
    private Map<Long, Integer> homeIdMap;
    private UserSerializer userSerializer;
    private StreamStartRegulator streamStartRegulator = new StreamStartRegulator();
    private static final int NO_HOME = -1;

    public DiscordEventAdapter(final EventListener listener, final Map<Long, Integer> homeIdMap,
                               final UserSerializer userSerializer) {
        this.listener = listener;
        this.homeIdMap = homeIdMap;
        this.userSerializer = userSerializer;
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull final PrivateMessageReceivedEvent event) {
        LOGGER.info("Got event with message: {} ", event.getMessage().getContentRaw());
        LOGGER.info("Got event with channel: {} ", event.getMessage().getChannel().getName());
        LOGGER.info("Got event with user: {} ", event.getAuthor().getName());
    }

    @Override
    public void onGuildMessageReceived(@Nonnull final GuildMessageReceivedEvent event) {
        try {
            int botHomeId = resolveHomeId(event.getGuild());
            if (botHomeId == NO_HOME) {
                return;
            }
            DiscordUser sender = getUser(event.getMember(), botHomeId);
            listener.onMessage(new DiscordMessageSentEvent(event, botHomeId, sender));
        } catch (BotErrorException e) {
            LOGGER.warn("Unhandled BotErrorException: {}", e.getErrorMessage());
        }
    }

    @Override
    public void onUserActivityEnd(@Nonnull final UserActivityEndEvent event) {
        int botHomeId = resolveHomeId(event.getGuild());
        if (botHomeId == NO_HOME) {
            return;
        }
        streamStartRegulator.endActivity(event, botHomeId);
    }

    @Override
    public void onUserActivityStart(@Nonnull final UserActivityStartEvent event) {
        int botHomeId = resolveHomeId(event.getGuild());
        if (botHomeId == NO_HOME) {
            return;
        }
        if (streamStartRegulator.startActivity(event, botHomeId)) {
            listener.onStreamStart(new DiscordStreamStartEvent(event, botHomeId));
        }
    }

    @Override
    public void onUserUpdateOnlineStatus(@Nonnull UserUpdateOnlineStatusEvent event) {
    }

    @Override
    public void onGuildMemberJoin(@Nonnull final GuildMemberJoinEvent event) {
        try {
            int botHomeId = resolveHomeId(event.getGuild());
            if (botHomeId == NO_HOME) {
                return;
            }
            DiscordUser member = getUser(event.getMember(), botHomeId);
            listener.onNewUser(new DiscordNewUserEvent(event, botHomeId, member));
        } catch (BotErrorException e) {
            LOGGER.warn("Unhandled BotErrorException: {}", e.getErrorMessage());
        }
    }

    private int resolveHomeId(final Guild guild) {
        if (!homeIdMap.containsKey(guild.getIdLong())) {
            return NO_HOME;
        }
        return homeIdMap.get(guild.getIdLong());
    }

    private DiscordUser getUser(final Member member, final int botHomeId) throws BotErrorException {
        boolean isModerator = false;
        for(Role role : member.getRoles()) {
            if (role.getPermissions().contains(Permission.KICK_MEMBERS)) {
                isModerator = true;
            }
        }

        DiscordUserStatus status = new DiscordUserStatus(isModerator, false, member.isOwner());
        HomedUser user = userSerializer.lookupByDiscordId(botHomeId, member.getIdLong(), member.getEffectiveName(),
                status);
        return new DiscordUser(user, member);
    }
}
