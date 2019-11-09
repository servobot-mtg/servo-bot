package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.events.Event;

public abstract class DiscordEvent implements Event {
    private int homeId;

    public DiscordEvent(final int homeId) {
        this.homeId = homeId;
    }

    @Override
    public int getHomeId() {
        return homeId;
    }
}
