package com.ryan_mtg.servobot.game;

import com.ryan_mtg.servobot.user.User;

public interface Responder {
    void respond(User user, String message);
}
