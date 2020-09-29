package com.ryan_mtg.servobot.model.editors;

import com.ryan_mtg.servobot.data.factories.GameQueueSerializer;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.game_queue.Game;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueEdit;
import com.ryan_mtg.servobot.model.game_queue.GameQueueTable;
import com.ryan_mtg.servobot.user.HomedUser;

import java.util.Arrays;
import java.util.function.Consumer;

public class GameQueueEditor {
    private final int contextId;
    private final GameQueueTable gameQueueTable;
    private final GameQueueSerializer gameQueueSerializer;

    public GameQueueEditor(final int contextId, final GameQueueTable gameQueueTable,
            final GameQueueSerializer gameQueueSerializer) {
        this.contextId = contextId;
        this.gameQueueTable = gameQueueTable;
        this.gameQueueSerializer = gameQueueSerializer;
    }

    public void createGameQueue(final Game game, final Consumer<GameQueue> gameQueueSavedCallback) throws UserError {
        GameQueue gameQueue = new GameQueue(GameQueue.UNREGISTERED_ID, game, GameQueue.State.IDLE,
                null, null, null, Arrays.asList());
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        gameQueueEdit.save(contextId, gameQueue, gameQueueSavedCallback);
        gameQueueSerializer.commit(gameQueueEdit);
    }

    public GameQueue getGameQueue(final int gameQueueId) {
        return gameQueueTable.getGameQueue(gameQueueId);
    }

    public void setMessage(final GameQueue gameQueue, final Message message) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        gameQueue.setMessage(message);
        gameQueueEdit.save(contextId, gameQueue);
        gameQueueSerializer.commit(gameQueueEdit);
    }

    public void setCodeAndServer(final int gameQueueId, final String code, final String server) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        GameQueue gameQueue = getGameQueue(gameQueueId);
        gameQueue.setCode(code);
        gameQueue.setServer(server);
        gameQueueEdit.save(contextId, gameQueue);
        gameQueueSerializer.commit(gameQueueEdit);
    }

    public void setCode(final int gameQueueId, final String code) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        GameQueue gameQueue = getGameQueue(gameQueueId);
        gameQueue.setCode(code);
        gameQueueEdit.save(contextId, gameQueue);
        gameQueueSerializer.commit(gameQueueEdit);
    }

    public void setServer(final int gameQueueId, final String server) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        GameQueue gameQueue = getGameQueue(gameQueueId);
        gameQueue.setServer(server);
        gameQueueEdit.save(contextId, gameQueue);
        gameQueueSerializer.commit(gameQueueEdit);
    }

    public void addUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit gameQueueEdit = gameQueue.enqueue(player);
        gameQueueSerializer.commit(gameQueueEdit);
    }

    public void dequeueUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit gameQueueEdit = gameQueue.dequeue(player);
        gameQueueSerializer.commit(gameQueueEdit);
    }

    public void clear(final int gameQueueId) {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit gameQueueEdit = gameQueue.clear(contextId);
        gameQueueSerializer.commit(gameQueueEdit);
    }
}
