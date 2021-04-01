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
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.HomedUserTable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
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

    public GameQueueTable createGameQueueTable(final int botHomeId, final HomedUserTable homedUserTable,
            final ServiceHome serviceHome) {
        GameQueueTable gameQueueTable = new GameQueueTable();

        for (GameQueueRow gameQueueRow : gameQueueRepository.findAllByBotHomeId(botHomeId)) {
            Message message = getMessage(serviceHome, gameQueueRow);

            int gameQueueId = gameQueueRow.getId();
            List<GameQueueEntry> gameQueueEntries = new ArrayList<>();
            for (GameQueueEntryRow gameQueueEntryRow : gameQueueEntryRepository.findByGameQueueId(gameQueueId)) {
                gameQueueEntries.add(createGameQueueEntry(homedUserTable, gameQueueEntryRow));
            }

            Instant startTime = gameQueueRow.getStartTime() == null ? null :
                    Instant.ofEpochMilli(gameQueueRow.getStartTime());
            GameQueue gameQueue = SystemError.filter(() -> new GameQueue(gameQueueId, gameQueueRow.getGame(),
                    gameQueueRow.getFlags(), gameQueueRow.getMinPlayers(), gameQueueRow.getMaxPlayers(),
                    gameQueueRow.getState(), gameQueueRow.getCode(), gameQueueRow.getServer(),
                    gameQueueRow.getProximityServer(), gameQueueRow.getGamerTagVariable(), startTime, message,
                    gameQueueEntries));

            gameQueueTable.add(gameQueue);
        }

        return gameQueueTable;
    }

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

        List<GameQueueEntryRow> gameQueueEntryRowsToDelete = new ArrayList<>();
        gameQueueEdit.getDeletedGameQueueEntries().forEach((gameQueueEntry, gameQueueId) ->
            gameQueueEntryRowsToDelete.add(createGameQueueEntryRow(gameQueueId, gameQueueEntry)));
        gameQueueEntryRepository.deleteAll(gameQueueEntryRowsToDelete);

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
        gameQueueRow.setFlags(gameQueue.getFlags());
        gameQueueRow.setMinPlayers(gameQueue.getMinPlayers());
        gameQueueRow.setMaxPlayers(gameQueue.getMaxPlayers());
        gameQueueRow.setState(gameQueue.getState());
        gameQueueRow.setStartTime(gameQueue.getStartTime() == null ? null : gameQueue.getStartTime().toEpochMilli());
        gameQueueRow.setCode(gameQueue.getCode());
        gameQueueRow.setServer(gameQueue.getServer());
        gameQueueRow.setProximityServer(gameQueue.getProximityServer());
        gameQueueRow.setGamerTagVariable(gameQueue.getGamerTagVariable());
        Message message = gameQueue.getMessage();
        gameQueueRow.setChannelId(message != null ? message.getChannelId() : 0);
        gameQueueRow.setMessageId(message != null ? message.getId() : 0);
        return gameQueueRow;
    }

    private GameQueueEntry createGameQueueEntry(final HomedUserTable homedUserTable,
            final GameQueueEntryRow gameQueueEntryRow) {
        HomedUser player = homedUserTable.getById(gameQueueEntryRow.getUserId());
        Instant enqueueTime = Instant.ofEpochMilli(gameQueueEntryRow.getEnqueueTime());
        return new GameQueueEntry(player, enqueueTime, gameQueueEntryRow.getState());
    }

    private GameQueueEntryRow createGameQueueEntryRow(final int gameQueueId, final GameQueueEntry gameQueueEntry) {
        GameQueueEntryRow gameQueueEntryRow = new GameQueueEntryRow();
        gameQueueEntryRow.setGameQueueId(gameQueueId);
        gameQueueEntryRow.setUserId(gameQueueEntry.getUser().getId());
        gameQueueEntryRow.setEnqueueTime(gameQueueEntry.getEnqueueTime().toEpochMilli());
        gameQueueEntryRow.setState(gameQueueEntry.getState());

        return gameQueueEntryRow;
    }

    private Message getMessage(final ServiceHome serviceHome, final GameQueueRow gameQueueRow) {
        if (gameQueueRow.getMessageId() != 0 && gameQueueRow.getChannelId() != 0) {
            return serviceHome.getSavedMessage(gameQueueRow.getChannelId(), gameQueueRow.getMessageId());
        }
        return null;
    }
}
