package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.model.game_queue.GameQueue;

import java.util.Collection;

public interface Context {
    int getContextId();
    Collection<Service> getServices();
    Collection<GameQueue> getGameQueues();
}
