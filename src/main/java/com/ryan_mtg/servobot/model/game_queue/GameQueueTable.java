package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.model.Message;

import java.util.ArrayList;
import java.util.List;

public class GameQueueTable {
    private final List<GameQueue> gameQueues = new ArrayList<>();

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

    public GameQueue matchesQueue(final Message message) {
        for (GameQueue gameQueue : gameQueues) {
            if (gameQueue.matches(message)) {
                return gameQueue;
            }
        }
        return null;
    }
}
