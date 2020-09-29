package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.GameQueueEntryRow;
import com.ryan_mtg.servobot.data.models.GameQueueRow;
import com.ryan_mtg.servobot.data.repositories.GameQueueEntryRepository;
import com.ryan_mtg.servobot.data.repositories.GameQueueRepository;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueEdit;
import com.ryan_mtg.servobot.model.game_queue.GameQueueEntry;
import com.ryan_mtg.servobot.model.game_queue.GameQueueTable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GameQueueSerializer {
    private final GameQueueRepository gameQueueRepository;
    private final GameQueueEntryRepository gameQueueEntryRepository;

    public GameQueueSerializer(final GameQueueRepository gameQueueRepository,
            final GameQueueEntryRepository gameQueueEntryRepository) {
        this.gameQueueRepository = gameQueueRepository;
        this.gameQueueEntryRepository = gameQueueEntryRepository;
    }

    private Message getMessage(final ServiceHome serviceHome, final GameQueueRow gameQueueRow) {
        if (gameQueueRow.getMessageId() != 0 && gameQueueRow.getChannelId() != 0) {
            return serviceHome.getSavedMessage(gameQueueRow.getChannelId(), gameQueueRow.getMessageId());
        }
        return null;
    }

    public GameQueueTable createGameQueueTable(final int botHomeId, final ServiceHome serviceHome) {
        GameQueueTable gameQueueTable = new GameQueueTable();

        for (GameQueueRow gameQueueRow : gameQueueRepository.findAllByBotHomeId(botHomeId)) {
            Message message = getMessage(serviceHome, gameQueueRow);

            GameQueue gameQueue = SystemError.filter(() -> new GameQueue(gameQueueRow.getId(), gameQueueRow.getGame(),
                    gameQueueRow.getState(), gameQueueRow.getCode(), gameQueueRow.getServer(), message));
            /*
            GameQueueEntryRepository gameQueueEntryRepository = serializers.getGameQueueEntryRepository();
            for (GameQueueEntryRow gameQueueEntryRow :
                    gameQueueEntryRepository.findByGameQueueIdOrderBySpotAsc(gameQueue.getId())) {
                gameQueue.enqueue(gameQueueEntryRow.getUserId(), gameQueueEntryRow.getSpot());
            }
             */

            gameQueueTable.add(gameQueue);
        }

        return gameQueueTable;
    }

    /*
    @Transactional(rollbackOn = Exception.class)
    private void saveGameQueue(final int botHomeId, final GameQueue gameQueue) {
        gameQueueRepository.save(createGameQueueRow(botHomeId, gameQueue));
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
    public void emptyGameQueue(final GameQueue gameQueue) {
        gameQueueEntryRepository.deleteAllByGameQueueId(gameQueue.getId());
        saveGameQueue(gameQueue);
    }
     */

    @Transactional(rollbackOn = Exception.class)
    public void commit(final GameQueueEdit gameQueueEdit) {
        List<GameQueueRow> gameQueueRows = new ArrayList<>();
        Map<GameQueueRow, GameQueue> gameQueueRowMap = new HashMap<>();
        gameQueueEdit.getSavedGameQueues().forEach((gameQueue, botHomeId) -> {
            GameQueueRow gameQueueRow = createGameQueueRow(botHomeId, gameQueue);
            gameQueueRows.add(gameQueueRow);
            gameQueueRowMap.put(gameQueueRow, gameQueue);
        });
        gameQueueRepository.saveAll(gameQueueRows);
        for (GameQueueRow gameQueueRow : gameQueueRows) {
            GameQueue gameQueue = gameQueueRowMap.get(gameQueueRow);
            gameQueue.setId(gameQueueRow.getId());
            if (gameQueueEdit.getGameQueueCallbackMap().containsKey(gameQueue)) {
                gameQueueEdit.getGameQueueCallbackMap().get(gameQueue).accept(gameQueue);
            }
        }

        gameQueueEdit.getDeletedGameQueueEntries().forEach((gameQueueEntry, gameQueueId) -> {
            gameQueueEntryRepository.deleteAllByGameQueueIdAndSpot(gameQueueId, gameQueueEntry.getSpot());
        });

        List<GameQueueEntryRow> gameQueueEntryRowsToSave = new ArrayList<>();
        gameQueueEdit.getSavedGameQueueEntries().forEach((gameQueueEntry, gameQueueId) ->
            gameQueueEntryRowsToSave.add(createGameQueueEntryRow(gameQueueId, gameQueueEntry))
        );
        gameQueueEntryRepository.saveAll(gameQueueEntryRowsToSave);
    }

    private GameQueueRow createGameQueueRow(final int botHomeId, final GameQueue gameQueue) {
        GameQueueRow gameQueueRow = new GameQueueRow();
        gameQueueRow.setId(gameQueue.getId());
        gameQueueRow.setBotHomeId(botHomeId);
        gameQueueRow.setGame(gameQueue.getGame());
        gameQueueRow.setState(gameQueue.getState());
        gameQueueRow.setCode(gameQueue.getCode());
        gameQueueRow.setServer(gameQueue.getServer());
        Message message = gameQueue.getMessage();
        gameQueueRow.setChannelId(message != null ? message.getChannelId() : 0);
        gameQueueRow.setMessageId(message != null ? message.getId() : 0);
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
