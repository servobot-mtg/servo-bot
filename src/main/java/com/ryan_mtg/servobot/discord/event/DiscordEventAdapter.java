package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.discord.model.DiscordUser;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;

public class DiscordEventAdapter extends ListenerAdapter {
    static Logger LOGGER = LoggerFactory.getLogger(DiscordEventAdapter.class);
    private EventListener listener;
    private Map<Long, Integer> homeIdMap;
    private UserSerializer userSerializer;

    public DiscordEventAdapter(final EventListener listener, final Map<Long, Integer> homeIdMap,
                               final UserSerializer userSerializer) {
        this.listener = listener;
        this.homeIdMap = homeIdMap;
        this.userSerializer = userSerializer;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull final GuildMessageReceivedEvent event) {
        try {
            int botHomeId = resolveHomeId(event.getGuild());
            DiscordUser sender = getUser(event.getMember(), botHomeId);
            listener.onMessage(new DiscordMessageSentEvent(event, botHomeId, sender));
        } catch (BotErrorException e) {
            LOGGER.warn("Unhandled BotErrorException: {}", e.getErrorMessage());
        }
    }

    @Override
    public void onUserActivityStart(@Nonnull final UserActivityStartEvent event) {
        if (event.getNewActivity().getType() == Activity.ActivityType.STREAMING && isStreamer(event)) {
            listener.onStreamStart(new DiscordStreamStartEvent(event, resolveHomeId(event.getGuild())));
        }
    }

    @Override
    public void onUserUpdateOnlineStatus(@Nonnull UserUpdateOnlineStatusEvent event) {
    }

    private boolean isStreamer(final GenericUserPresenceEvent event) {
        return event.getMember().getIdLong() == event.getGuild().getOwnerIdLong();
    }

    private int resolveHomeId(final Guild guild) {
        return homeIdMap.get(guild.getIdLong());
    }

    private DiscordUser getUser(final Member member, final int botHomeId) {
        User user = userSerializer.lookupByDiscordId(member.getIdLong());
        LOGGER.info("User is {} with id {}", user.getTwitchUsername(), user.getId());
        UserStatus userStatus = userSerializer.getStatus(user, botHomeId);
        return new DiscordUser(user, member, userStatus);
    }
}
