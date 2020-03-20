package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.ryan_mtg.servobot.events.Event;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.HomeEditor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class TwitchEvent implements Event {
    @Getter(value = AccessLevel.PROTECTED)
    private TwitchClient client;

    @Getter @Setter
    private BotEditor botEditor;

    @Getter
    private int homeId;

    @Getter @Setter
    private HomeEditor homeEditor;

    public TwitchEvent(final TwitchClient client, final int homeId) {
        this.client = client;
        this.homeId = homeId;
    }
}
