package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.twitch.model.TwitchChannel;
import com.ryan_mtg.servobot.twitch.model.TwitchService;

public class TwitchHomeEvent extends TwitchEvent implements HomeEvent {
    private String channelName;
    private TwitchChannel twitchChannel;

    public TwitchHomeEvent(final TwitchClient client, final int botHomeId, final String channelName) {
        super(client, botHomeId);
        this.channelName = channelName;
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
            twitchChannel = new TwitchChannel(getClient(), channelName, getHomeEditor());
        }
        return twitchChannel;
    }
}
