package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;

public interface NewUserEvent extends Event {
    Home getHome();
    User getUser();
}
