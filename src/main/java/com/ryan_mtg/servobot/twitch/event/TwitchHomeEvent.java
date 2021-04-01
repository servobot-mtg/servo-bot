package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.twitch.model.TwitchChannel;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.twitch.model.TwitchServiceHome;

public class TwitchHomeEvent extends TwitchEvent implements HomeEvent {
    private final String channelName;
    private final long channelId;
    private TwitchChannel twitchChannel;

    public TwitchHomeEvent(final TwitchClient client, final BotHome botHome, final String channelName,
            final long channelId) {
        super(client, botHome);
        this.channelName = channelName;
        this.channelId = channelId;
    }

    @Override
    public int getServiceType() {
        return TwitchService.TYPE;
    }

    public TwitchChannel getChannel() {
        if (twitchChannel == null) {
            twitchChannel = new TwitchChannel(getClient(), (TwitchServiceHome) getServiceHome(), channelName,
                    channelId);
        }
        return twitchChannel;
    }
}
