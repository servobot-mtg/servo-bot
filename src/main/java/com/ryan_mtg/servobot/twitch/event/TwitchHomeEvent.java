package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.twitch.model.TwitchChannel;
import com.ryan_mtg.servobot.twitch.model.TwitchService;

public class TwitchHomeEvent extends TwitchEvent implements HomeEvent {
    private AbstractChannelEvent event;
    private TwitchChannel twitchChannel;

    public TwitchHomeEvent(final TwitchClient client, final int botHomeId, final AbstractChannelEvent event) {
        super(client, botHomeId);
        this.event = event;
    }

    @Override
    public Home getHome() {
        return getChannel();
    }

    @Override
    public int getServiceType() {
        return TwitchService.TYPE;
    }

    public TwitchChannel getChannel() {
        if (twitchChannel == null) {
            twitchChannel = new TwitchChannel(getClient(), event.getChannel().getName(), getHomeEditor());
        }
        return twitchChannel;
    }
}
