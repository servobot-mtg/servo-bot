package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.ryan_mtg.servobot.events.Event;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.HomeEditor;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class TwitchEvent implements Event {
    @Getter(value = AccessLevel.PROTECTED)
    private TwitchClient client;
    private BotEditor botEditor;
    private int botHomeId;
    private HomeEditor homeEditor;

    public TwitchEvent(final TwitchClient client, final int botHomeId) {
        this.client = client;
        this.botHomeId = botHomeId;
    }

    @Override
    public BotEditor getBotEditor() {
        return botEditor;
    }

    @Override
    public void setBotEditor(final BotEditor botEditor) {
        this.botEditor = botEditor;
    }

    @Override
    public int getHomeId() {
        return 0;
    }

    @Override
    public HomeEditor getHomeEditor() {
        return homeEditor;
    }

    @Override
    public void setHomeEditor(final HomeEditor homeEditor) {
        this.homeEditor = homeEditor;
    }
}
