package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.events.Event;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.HomeEditor;

public abstract class DiscordEvent implements Event {
    private int homeId;
    private BotEditor botEditor;
    private HomeEditor homeEditor;

    public DiscordEvent(final int homeId) {
        this.homeId = homeId;
    }

    @Override
    public int getHomeId() {
        return homeId;
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
    public HomeEditor getHomeEditor() {
        return homeEditor;
    }

    @Override
    public void setHomeEditor(final HomeEditor homeEditor) {
        this.homeEditor = homeEditor;
    }
}
