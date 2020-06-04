package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.ryan_mtg.servobot.events.UserHomeEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.twitch.model.TwitchUser;

public class TwitchRaidEvent extends TwitchHomeEvent implements UserHomeEvent {
    private TwitchUser raider;

    public TwitchRaidEvent(final TwitchClient client, final RaidEvent event, final BotHome botHome,
                           final TwitchUser raider) {
        super(client, botHome, event.getChannel().getName());
        this.raider = raider;
    }

    @Override
    public User getUser() {
        return raider;
    }
}
