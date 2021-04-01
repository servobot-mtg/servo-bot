package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.ryan_mtg.servobot.events.BotHomeEvent;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class TwitchEvent implements BotHomeEvent {
    @Getter(value = AccessLevel.PROTECTED)
    private final TwitchClient client;

    @Getter @Setter
    private BotEditor botEditor;

    private final BotHome botHome;

    @Getter @Setter
    private HomeEditor homeEditor;

    public TwitchEvent(final TwitchClient client, final BotHome botHome) {
        this.client = client;
        this.botHome = botHome;
    }

    @Override
    public int getHomeId() {
        return botHome.getId();
    }

    @Override
    public ServiceHome getServiceHome() {
        return botHome.getServiceHome(TwitchService.TYPE);
    }

    @Override
    public ServiceHome getServiceHome(final int serviceType) {
        return botHome.getServiceHome(serviceType);
    }
}
