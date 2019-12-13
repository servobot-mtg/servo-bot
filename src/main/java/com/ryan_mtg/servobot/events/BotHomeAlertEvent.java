package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;

public class BotHomeAlertEvent implements AlertEvent {
    private int homeId;
    private String alertToken;
    private Home home;
    private BotEditor botEditor;
    private HomeEditor homeEditor;

    public BotHomeAlertEvent(final int homeId, final String alertToken, final Home home) {
        this.alertToken = alertToken;
        this.homeId = homeId;
        this.home = home;
    }

    @Override
    public int getHomeId() {
        return homeId;
    }

    @Override
    public String getAlertToken() {
        return alertToken;
    }

    @Override
    public Home getHome() {
        return home;
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
