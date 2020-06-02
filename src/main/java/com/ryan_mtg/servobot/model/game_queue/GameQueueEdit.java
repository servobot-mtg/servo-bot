package com.ryan_mtg.servobot.model.game_queue;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GameQueueEdit {
    private List<GameQueue> savedGameQueues = new ArrayList<>();
    private Map<GameQueueEntry, Integer> savedGameQueueEntries = new HashMap<>();
    private Map<GameQueueEntry, Integer> deletedGameQueueEntries = new HashMap<>();

    public void save(final GameQueue gameQueue) {
        savedGameQueues.add(gameQueue);
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
        savedGameQueues.addAll(gameQueueEdit.getSavedGameQueues());
    }
}
