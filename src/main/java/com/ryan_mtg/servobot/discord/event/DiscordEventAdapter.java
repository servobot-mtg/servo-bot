package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.events.EventListener;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Map;

public class DiscordEventAdapter extends ListenerAdapter {
    private EventListener listener;
    private Map<Long, Integer> homeIdMap;

    public DiscordEventAdapter(final EventListener listener, final Map<Long, Integer> homeIdMap) {
        this.listener = listener;
        this.homeIdMap = homeIdMap;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull final GuildMessageReceivedEvent event) {
        listener.onMessage(new DiscordMessageSentEvent(event, resolveHomeId(event.getGuild())));
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
}
