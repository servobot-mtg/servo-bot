package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.events.Event;
import com.ryan_mtg.servobot.model.BotEditor;
import lombok.Getter;
import lombok.Setter;

public abstract class DiscordEvent implements Event {
    @Getter @Setter
    private BotEditor botEditor;

    public DiscordEvent() {
    }
}
