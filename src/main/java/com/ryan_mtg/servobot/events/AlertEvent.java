package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Home;

public interface AlertEvent extends Event {
    String getAlertToken();

    Home getHome();
}
