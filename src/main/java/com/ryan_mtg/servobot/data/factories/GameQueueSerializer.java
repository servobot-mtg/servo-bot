package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.GameQueueEntryRow;
import com.ryan_mtg.servobot.data.models.GameQueueRow;
import com.ryan_mtg.servobot.data.repositories.GameQueueEntryRepository;
import com.ryan_mtg.servobot.data.repositories.GameQueueRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.GameQueue;
import com.ryan_mtg.servobot.model.GameQueueEntry;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class GameQueueSerializer {
    private final GameQueueRepository gameQueueRepository;
    private final GameQueueEntryRepository gameQueueEntryRepository;

    public GameQueueSerializer(final GameQueueRepository gameQueueRepository,
            final GameQueueEntryRepository gameQueueEntryRepository) {
        this.gameQueueRepository = gameQueueRepository;
        this.gameQueueEntryRepository = gameQueueEntryRepository;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void saveGameQueue(final GameQueue gameQueue) {
        GameQueueRow gameQueueRow = gameQueueRepository.findById(gameQueue.getId());
        gameQueueRow.setState(gameQueue.getState());
        gameQueueRow.setCurrentPlayerId(gameQueue.getCurrentPlayerId());
        gameQueueRow.setNext(gameQueue.getNext());
        if (!gameQueue.getName().equals(gameQueueRow.getName())) {
            gameQueueRow.setName(gameQueue.getName());
        }
        gameQueueRepository.save(gameQueueRow);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void removeEntry(final GameQueue gameQueue, final int userId) {
        gameQueueEntryRepository.deleteAllByGameQueueIdAndUserId(gameQueue.getId(), userId);
        saveGameQueue(gameQueue);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void addEntry(final GameQueue gameQueue, final GameQueueEntry gameQueueEntry) {
        GameQueueEntryRow gameQueueEntryRow = new GameQueueEntryRow();
        gameQueueEntryRow.setGameQueueId(gameQueue.getId());
        gameQueueEntryRow.setSpot(gameQueueEntry.getSpot());
        gameQueueEntryRow.setUserId(gameQueueEntry.getUserId());
        gameQueueEntryRepository.save(gameQueueEntryRow);

        saveGameQueue(gameQueue);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void emptyGameQueue(GameQueue gameQueue) {
        gameQueueEntryRepository.deleteAllByGameQueueId(gameQueue.getId());
        saveGameQueue(gameQueue);
    }
}
