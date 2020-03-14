package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.User;

public interface NewUserEvent extends HomeEvent {
    User getUser();
}
