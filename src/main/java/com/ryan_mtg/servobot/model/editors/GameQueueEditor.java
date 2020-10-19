package com.ryan_mtg.servobot.model.editors;

import com.ryan_mtg.servobot.data.factories.GameQueueSerializer;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.game_queue.Game;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueAction;
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
        gameQueueTable.add(gameQueue);
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

    public GameQueueAction setCodeAndServer(final int gameQueueId, final String code, final String server) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        GameQueue gameQueue = getGameQueue(gameQueueId);
        gameQueue.setCode(code);
        gameQueue.setServer(server);
        gameQueueEdit.save(contextId, gameQueue);
        gameQueueSerializer.commit(gameQueueEdit);
        return GameQueueAction.codeChanged(gameQueue.getCode(), gameQueue.getServer());
    }

    public GameQueueAction setCode(final int gameQueueId, final String code) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        GameQueue gameQueue = getGameQueue(gameQueueId);
        gameQueue.setCode(code);
        gameQueueEdit.save(contextId, gameQueue);
        gameQueueSerializer.commit(gameQueueEdit);
        return GameQueueAction.codeChanged(gameQueue.getCode(), gameQueue.getServer());
    }

    public GameQueueAction setServer(final int gameQueueId, final String server) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        GameQueue gameQueue = getGameQueue(gameQueueId);
        gameQueue.setServer(server);
        gameQueueEdit.save(contextId, gameQueue);
        gameQueueSerializer.commit(gameQueueEdit);
        return GameQueueAction.codeChanged(gameQueue.getCode(), gameQueue.getServer());
    }

    public GameQueueAction addUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.enqueue(player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction dequeueUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.dequeue(player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public void clear(final int gameQueueId) {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit gameQueueEdit = gameQueue.clear(contextId);
        gameQueueSerializer.commit(gameQueueEdit);
    }

    public GameQueueAction readyUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.ready(player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction lgUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.lg(player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction rotateUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.rotate(player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction moveUser(final int gameQueueId, final HomedUser player, final int position)
            throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit gameQueueEdit = gameQueue.move(player, position);
        gameQueueSerializer.commit(gameQueueEdit);
        return GameQueueAction.playerMoved(player);
    }
}
