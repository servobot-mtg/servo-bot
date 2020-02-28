package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.user.User;

public interface Service {
    int NO_SERVICE_TYPE = 0;

    int getType();
    String getName();
    void register(BotHome botHome);
    void unregister(BotHome home);

    void start(EventListener eventListener) throws Exception;

    void whisper(User user, String message);
}
