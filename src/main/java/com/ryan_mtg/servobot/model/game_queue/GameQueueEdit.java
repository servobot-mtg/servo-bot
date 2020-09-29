package com.ryan_mtg.servobot.model.game_queue;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class GameQueueEdit {
    private Map<GameQueue, Integer> savedGameQueues = new HashMap<>();
    private Map<GameQueueEntry, Integer> savedGameQueueEntries = new HashMap<>();
    private Map<GameQueueEntry, Integer> deletedGameQueueEntries = new HashMap<>();
    private Map<GameQueue, Consumer<GameQueue>> gameQueueCallbackMap = new HashMap<>();

    public void save(final int botHomeId, final GameQueue gameQueue) {
        savedGameQueues.put(gameQueue, botHomeId);
    }

    public void save(final int botHomeId, final GameQueue gameQueue, final Consumer<GameQueue> gameQueueCallback) {
        save(botHomeId, gameQueue);
        gameQueueCallbackMap.put(gameQueue, gameQueueCallback);
    }

    public void save(final int gameQueueId, final GameQueueEntry gameQueueEntry) {
        savedGameQueueEntries.put(gameQueueEntry, gameQueueId);
    }

    public void delete(final int gameQueueId, final GameQueueEntry gameQueueEntry) {
        deletedGameQueueEntries.put(gameQueueEntry, gameQueueId);
    }

    public void merge(final GameQueueEdit gameQueueEdit) {
        savedGameQueueEntries.putAll(gameQueueEdit.getSavedGameQueueEntries());
        deletedGameQueueEntries.putAll(gameQueueEdit.getDeletedGameQueueEntries());
        savedGameQueues.putAll(gameQueueEdit.getSavedGameQueues());
        gameQueueCallbackMap.putAll(gameQueueEdit.getGameQueueCallbackMap());
    }
}
