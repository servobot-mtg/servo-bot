package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private List<Entry> queue = new ArrayList<>();
    private Map<Integer, Entry> userMap = new HashMap<>();

    public enum State {
        IDLE,
        PLAYING,
        CLOSED,
    }

    public GameQueue(final int id, final Game game, final State state, final String code, final String server,
            final Message message) throws UserError {
        this.id = id;
        this.game = game;
        this.state = state;
        this.code = code;
        this.server = server;
        this.message = message;

        Validation.validateStringLength(code, Validation.MAX_NAME_LENGTH, "Code");
        Validation.validateStringLength(server, Validation.MAX_NAME_LENGTH, "Server");
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

    public GameQueueEntry enqueue(final int userId) {
        Entry entry = new Entry(userId, nextSpot);
        queue.add(entry);
        userMap.put(userId, entry);

        return new GameQueueEntry(userId, nextSpot++, queue.size());
    }

    public void enqueue(final int userId, final int spot) {
        Entry entry = new Entry(userId, spot);
        queue.add(entry);
        userMap.put(userId, entry);
        nextSpot = Math.max(spot + 1, nextSpot);
    }
     */

    public GameQueueEdit mergeUser(final int newUserId, final List<Integer> oldUserIds) {
        //TODO: fix
        GameQueueEdit gameQueueEdit = new GameQueueEdit();
        /*
        if (oldUserIds.contains(currentPlayerId)) {
            currentPlayerId = newUserId;
            gameQueueEdit.save(this);
        }
         */

        Entry entry = null;
        for (int oldUserId : oldUserIds) {
            if (userMap.containsKey(oldUserId)) {
                Entry oldEntry = userMap.get(oldUserId);
                if (entry == null) {
                    entry = oldEntry;
                } else {
                    if (oldEntry.getSpot() < entry.getSpot()) {
                        Entry temp = oldEntry;
                        oldEntry = entry;
                        entry = temp;
                    }

                    gameQueueEdit.delete(id, new GameQueueEntry(oldEntry.getUserId(), oldEntry.getSpot(), 0));
                }
            }
        }

        if (entry != null) {
            entry.setUserId(newUserId);
            userMap.put(newUserId, entry);
            gameQueueEdit.save(id, new GameQueueEntry(newUserId, entry.getSpot(), 0));
        }

        oldUserIds.forEach(oldUserId -> userMap.remove(oldUserId));

        return gameQueueEdit;
    }

    /*
    public boolean contains(final int userId) {
        return currentPlayerId == userId || userMap.containsKey(userId);
    }
     */

    @Getter @Setter
    private static final class Entry {
        private int userId;
        private int spot;
        private Instant joinTime;

        Entry(final int userId, final int spot, final Instant joinTime) {
            this.userId = userId;
            this.spot = spot;
            this.joinTime = joinTime;
        }
    }
}
