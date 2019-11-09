package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.EventListener;

public interface Service {
    void register(BotHome botHome);
    void start(EventListener eventListener) throws Exception;
}
