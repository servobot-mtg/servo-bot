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

import java.time.Instant;
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
        GameQueue gameQueue = new GameQueue(GameQueue.UNREGISTERED_ID, game, 0, GameQueue.State.IDLE,
                null, null, null, null, null, Arrays.asList());
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

    public GameQueueAction setCode(final int gameQueueId, final String code, final String server,
                                   final Boolean onBeta) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        GameQueue gameQueue = getGameQueue(gameQueueId);
        if (code != null) {
            gameQueue.setCode(code);
        }
        if (server != null) {
            gameQueue.setServer(server);
        }
        if (onBeta != null) {
            gameQueue.setVersion(onBeta);
        }
        gameQueueEdit.save(contextId, gameQueue);
        gameQueueSerializer.commit(gameQueueEdit);
        return GameQueueAction.codeChanged(gameQueue.getCode(), gameQueue.getServer(), gameQueue.isOnBeta());
    }

    public GameQueueAction setProximityServer(final int gameQueueId, final String proximityServer) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        GameQueue gameQueue = getGameQueue(gameQueueId);
        gameQueue.setProximityServer(proximityServer);
        gameQueueEdit.save(contextId, gameQueue);
        gameQueueSerializer.commit(gameQueueEdit);
        if (proximityServer == null) {
            return GameQueueAction.proximityServerChanged("turned off");
        }
        return GameQueueAction.proximityServerChanged(gameQueue.getProximityServer());
    }

    public GameQueueAction addUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.enqueue(contextId, player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction dequeueUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.dequeue(contextId, player, edit);
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

    public GameQueueAction unreadyUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.unready(player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction cutUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.cut(player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction lgUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.lg(contextId, player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction lgAll(final int gameQueueId) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.lgAll(contextId, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction permanentUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.permanent(contextId, player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction onCallUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.onCall(contextId, player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction rotateUser(final int gameQueueId, final HomedUser player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.rotate(contextId, player, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction rotateLg(final int gameQueueId) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.rotateLg(contextId, edit);
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

    public GameQueueAction rsvpUser(final int gameQueueId, final HomedUser player, final Instant rsvpTime)
            throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueEdit edit = new GameQueueEdit();
        GameQueueAction action = gameQueue.rsvp(player, rsvpTime, edit);
        gameQueueSerializer.commit(edit);
        return action;
    }

    public GameQueueAction schedule(final int gameQueueId, final Instant startTime) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        GameQueue gameQueue = getGameQueue(gameQueueId);
        gameQueue.setStartTime(startTime);

        gameQueueEdit.save(contextId, gameQueue);
        gameQueueSerializer.commit(gameQueueEdit);
        return GameQueueAction.startTimeChanged(gameQueue.getStartTime());
    }

    public GameQueueAction start(final int gameQueueId) throws UserError {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        GameQueue gameQueue = getGameQueue(gameQueueId);
        GameQueueAction action = gameQueue.start(contextId, gameQueueEdit);
        gameQueueSerializer.commit(gameQueueEdit);
        return action;
    }
}