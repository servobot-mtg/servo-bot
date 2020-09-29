package com.ryan_mtg.servobot.model.editors;

import com.ryan_mtg.servobot.data.factories.GameQueueSerializer;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.game_queue.Game;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueEdit;
import com.ryan_mtg.servobot.model.game_queue.GameQueueTable;

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
                null, null, null);
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
}
