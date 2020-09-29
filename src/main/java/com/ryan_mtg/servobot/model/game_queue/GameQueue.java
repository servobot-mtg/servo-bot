package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.HomedUserTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameQueue {
    public static final int UNREGISTERED_ID = 0;

    @Getter @Setter
    private int id;

    @Getter
    private Game game;

    @Getter @Setter
    private String code;

    @Getter @Setter
    private String server;

    @Getter @Setter
    private State state;

    @Getter @Setter
    private Message message;

    private List<GameQueueEntry> playing = new ArrayList<>();
    private List<GameQueueEntry> waitQueue = new ArrayList<>();
    private Map<Integer, GameQueueEntry> userMap = new HashMap<>();

    public enum State {
        IDLE,
        PLAYING,
        CLOSED,
    }

    public GameQueue(final int id, final Game game, final State state, final String code, final String server,
            final Message message, final List<GameQueueEntry> gameQueueEntries) throws UserError {
        this.id = id;
        this.game = game;
        this.state = state;
        this.code = code;
        this.server = server;
        this.message = message;

        Validation.validateStringLength(code, Validation.MAX_NAME_LENGTH, "Code");
        Validation.validateStringLength(server, Validation.MAX_NAME_LENGTH, "Server");

        for (GameQueueEntry gameQueueEntry : gameQueueEntries) {
            userMap.put(gameQueueEntry.getUser().getId(), gameQueueEntry);
            switch (gameQueueEntry.getState()) {
                case WAITING:
                    waitQueue.add(gameQueueEntry);
                    break;
                case PLAYING:
                    playing.add(gameQueueEntry);
                    break;
            }
        }
    }

    public GameQueueEdit enqueue(final HomedUser player) throws UserError {
        if (userMap.containsKey(player.getId())) {
            throw new UserError("%s is already queued.", player.getName());
        }

        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        GameQueueEntry newEntry = new GameQueueEntry(player, Instant.now(), PlayerState.WAITING);
        gameQueueEdit.save(getId(), newEntry);
        waitQueue.add(newEntry);
        userMap.put(player.getId(), newEntry);

        promotePlayersToGame(gameQueueEdit);

        return gameQueueEdit;
    }

    public GameQueueEdit dequeue(final HomedUser player) throws UserError {
        int playerId = player.getId();
        if (!userMap.containsKey(playerId)) {
            throw new UserError("%s is not already queued.", player.getName());
        }

        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        removeEntry(gameQueueEdit, playerId);
        promotePlayersToGame(gameQueueEdit);

        return gameQueueEdit;
    }

    public GameQueueEdit clear(final int botHomeId) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();

        for (GameQueueEntry entry : userMap.values()) {
            gameQueueEdit.delete(id, entry);
        }

        code = null;
        server = null;

        playing.clear();
        waitQueue.clear();
        userMap.clear();

        gameQueueEdit.save(botHomeId, this);

        return gameQueueEdit;
    }


    private void removeEntry(final GameQueueEdit gameQueueEdit, final int playerId) {
        GameQueueEntry entry = userMap.get(playerId);
        switch (entry.getState()) {
            case PLAYING:
                playing.remove(entry);
                break;
            case WAITING:
                waitQueue.remove(entry);
                break;
        }
        gameQueueEdit.delete(id, entry);
        userMap.remove(playerId);
    }

    public List<HomedUser> getGamePlayers() {
        return makeList(playing);
    }

    public List<HomedUser> getWaitQueue() {
        return makeList(waitQueue);
    }


    /*

    public void setState(final State state) {
        if (this.state == State.IDLE && state == State.PLAYING) {
            nextSpot = 1;
            currentPlayerId = EMPTY_QUEUE;
            queue.clear();
        }
        this.state = state;
    }

    public int getNext() {
        return nextSpot;
    }

    public int pop() {
        if (queue.isEmpty()) {
            return currentPlayerId = EMPTY_QUEUE;
        }

        Entry entry = queue.remove();
        userMap.remove(entry.getUserId());
        return currentPlayerId = entry.getUserId();
    }

    public List<GameQueueEntry> getFullQueue() {
        List<GameQueueEntry> response = new ArrayList<>();
        int position = 1;
        for (Entry entry : queue) {
            response.add(new GameQueueEntry(entry.getUserId(), entry.getSpot(), position++));
        }
        return response;
    }

    public void remove(final int playerId) {
        queue.removeIf(entry -> entry.getUserId() == playerId);
    }

    public void enqueue(final int userId, final int spot) {
        Entry entry = new Entry(userId, spot);
        queue.add(entry);
        userMap.put(userId, entry);
        nextSpot = Math.max(spot + 1, nextSpot);
    }
     */

    public GameQueueEdit mergeUser(final HomedUserTable homedUserTable, final int newUserId,
            final List<Integer> oldUserIds) {
        GameQueueEdit gameQueueEdit = new GameQueueEdit();

        Instant minEnqueueTime = Instant.MAX;
        PlayerState playerState = PlayerState.WAITING;
        boolean hasOldPlayer = false;
        for (int oldUserId : oldUserIds) {
            if (userMap.containsKey(oldUserId)) {
                GameQueueEntry oldEntry = userMap.get(oldUserId);
                if (oldEntry != null) {
                    hasOldPlayer = true;

                    if (oldEntry.getEnqueueTime().compareTo(minEnqueueTime) < 0) {
                        minEnqueueTime = oldEntry.getEnqueueTime();
                    }

                    if (oldEntry.getState() == PlayerState.PLAYING) {
                        playerState = PlayerState.PLAYING;
                    }

                    removeEntry(gameQueueEdit, oldUserId);
                }
            }
        }

        if (hasOldPlayer) {
            GameQueueEntry newEntry = userMap.get(newUserId);
            if (newEntry == null) {
                newEntry.setUser(homedUserTable.getById(newUserId));
                if (minEnqueueTime.compareTo(newEntry.getEnqueueTime()) < 0) {
                    newEntry.setEnqueueTime(minEnqueueTime);
                }

                if (playerState == PlayerState.PLAYING) {
                    newEntry.setState(playerState);
                }
            } else {
                newEntry = new GameQueueEntry(homedUserTable.getById(newUserId), minEnqueueTime, playerState);
                userMap.put(newUserId, newEntry);
            }
            gameQueueEdit.save(id, newEntry);
        }

        return gameQueueEdit;
    }

    private void promotePlayersToGame(final GameQueueEdit gameQueueEdit) {
        if (waitQueue.size() >= game.getMinPlayers() && playing.isEmpty()) {
            Collections.sort(waitQueue);

            while (!waitQueue.isEmpty() && playing.size() < game.getMaxPlayers()) {
                GameQueueEntry joiningEntry = waitQueue.remove(0);
                joiningEntry.setState(PlayerState.PLAYING);
                gameQueueEdit.save(getId(), joiningEntry);
                playing.add(joiningEntry);
            }
        }
    }

    private List<HomedUser> makeList(final List<GameQueueEntry> gameQueueEntries) {
        Collections.sort(gameQueueEntries);
        return gameQueueEntries.stream().map(entry -> entry.getUser()).collect(Collectors.toList());
    }
}
