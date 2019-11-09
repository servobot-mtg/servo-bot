package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Home;

public interface StreamStartEvent extends Event {
    Home getHome();
}
