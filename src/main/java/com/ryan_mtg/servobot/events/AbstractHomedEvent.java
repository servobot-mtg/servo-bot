package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;

public abstract class AbstractHomedEvent implements Event {
    private BotEditor botEditor;
    private HomeEditor homeEditor;
    private int homeId;

    protected AbstractHomedEvent(final int homeId) {
        this.homeId = homeId;
    }

    public abstract Home getHome();

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
    public void setHomeEditor(HomeEditor homeEditor) {
        this.homeEditor = homeEditor;
    }
}
