package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.model.game_queue.GameQueueTable;

import java.util.Collection;

public interface Context {
    int getContextId();
    Collection<Service> getServices();
    GameQueueTable getGameQueueTable();
    String getImageUrl();
}
