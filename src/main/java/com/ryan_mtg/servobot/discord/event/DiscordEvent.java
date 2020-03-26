package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.events.Event;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.HomeEditor;
import lombok.Getter;
import lombok.Setter;

public abstract class DiscordEvent implements Event {
    @Getter
    private int homeId;

    @Getter @Setter
    private BotEditor botEditor;

    @Getter @Setter
    private HomeEditor homeEditor;

    public DiscordEvent(final int homeId) {
        this.homeId = homeId;
    }
}
