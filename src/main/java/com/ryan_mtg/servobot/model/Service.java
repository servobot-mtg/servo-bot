package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.EventListener;

public interface Service {
    int getType();
    String getName();
    void register(BotHome botHome);
    void unregister(BotHome home);

    void start(EventListener eventListener) throws Exception;
}
