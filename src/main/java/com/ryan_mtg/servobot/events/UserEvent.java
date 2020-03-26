package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.User;

public interface UserEvent extends HomeEvent {
    User getUser();
}
