package com.ryan_mtg.servobot.model.game_queue;

import java.util.ArrayList;
import java.util.List;

public class GameQueueTable {
    private List<GameQueue> gameQueues = new ArrayList<>();

    public void add(final GameQueue gameQueue) {
        gameQueues.add(gameQueue);
    }

    public GameQueue getGameQueue(final int gameQueueId) {
        for (GameQueue gameQueue : gameQueues) {
            if (gameQueue.getId() == gameQueueId) {
                return gameQueue;
            }
        }
        return null;
    }
}
