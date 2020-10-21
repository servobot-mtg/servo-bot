package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Emote;

public interface EmoteHomeEvent extends MessageHomeEvent {
    Emote getEmote();
}

