package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.GameQueueEntryRow;
import com.ryan_mtg.servobot.data.models.GameQueueRow;
import com.ryan_mtg.servobot.data.repositories.GameQueueEntryRepository;
import com.ryan_mtg.servobot.data.repositories.GameQueueRepository;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueEdit;
import com.ryan_mtg.servobot.model.game_queue.GameQueueEntry;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class GameQueueSerializer {
    private final GameQueueRepository gameQueueRepository;
    private final GameQueueEntryRepository gameQueueEntryRepository;

    public GameQueueSerializer(final GameQueueRepository gameQueueRepository,
            final GameQueueEntryRepository gameQueueEntryRepository) {
        this.gameQueueRepository = gameQueueRepository;
        this.gameQueueEntryRepository = gameQueueEntryRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public void saveGameQueue(final GameQueue gameQueue) {
        gameQueueRepository.save(createGameQueueRow(gameQueue));
    }

    @Transactional(rollbackOn = Exception.class)
    public void removeEntry(final GameQueue gameQueue, final int userId) {
        gameQueueEntryRepository.deleteAllByGameQueueIdAndUserId(gameQueue.getId(), userId);
        saveGameQueue(gameQueue);
    }

    @Transactional(rollbackOn = Exception.class)
    public void addEntry(final GameQueue gameQueue, final GameQueueEntry gameQueueEntry) {
        gameQueueEntryRepository.save(createGameQueueEntryRow(gameQueue.getId(), gameQueueEntry));

        saveGameQueue(gameQueue);
    }

    @Transactional(rollbackOn = Exception.class)
    public void emptyGameQueue(GameQueue gameQueue) {
        gameQueueEntryRepository.deleteAllByGameQueueId(gameQueue.getId());
        saveGameQueue(gameQueue);
    }

    @Transactional(rollbackOn = Exception.class)
    public void commit(final GameQueueEdit gameQueueEdit) {
        List<GameQueueRow> gameQueueRows = new ArrayList<>();
        gameQueueEdit.getSavedGameQueues().forEach(gameQueue -> gameQueueRows.add(createGameQueueRow(gameQueue)));
        gameQueueRepository.saveAll(gameQueueRows);

        gameQueueEdit.getDeletedGameQueueEntries().forEach((gameQueueEntry, gameQueueId) -> {
            gameQueueEntryRepository.deleteAllByGameQueueIdAndSpot(gameQueueId, gameQueueEntry.getSpot());
        });

        List<GameQueueEntryRow> gameQueueEntryRowsToSave = new ArrayList<>();
        gameQueueEdit.getSavedGameQueueEntries().forEach((gameQueueEntry, gameQueueId) ->
            gameQueueEntryRowsToSave.add(createGameQueueEntryRow(gameQueueId, gameQueueEntry))
        );
        gameQueueEntryRepository.saveAll(gameQueueEntryRowsToSave);
    }

    private GameQueueRow createGameQueueRow(final GameQueue gameQueue) {
        //TODO: fix
        GameQueueRow gameQueueRow = gameQueueRepository.findById(gameQueue.getId());
        /*
        gameQueueRow.setState(gameQueue.getState());
        gameQueueRow.setCurrentPlayerId(gameQueue.getCurrentPlayerId());
        gameQueueRow.setNext(gameQueue.getNext());
        if (!gameQueue.getName().equals(gameQueueRow.getName())) {
            gameQueueRow.setName(gameQueue.getName());
        }
         */
        return gameQueueRow;
    }

    public GameQueueEntryRow createGameQueueEntryRow(final int gameQueueId, final GameQueueEntry gameQueueEntry) {
        GameQueueEntryRow gameQueueEntryRow = new GameQueueEntryRow();
        gameQueueEntryRow.setGameQueueId(gameQueueId);
        gameQueueEntryRow.setSpot(gameQueueEntry.getSpot());
        gameQueueEntryRow.setUserId(gameQueueEntry.getUserId());

        return gameQueueEntryRow;
    }
}
