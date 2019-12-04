package com.ryan_mtg.servobot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class GameQueue {
    public static final int EMPTY_QUEUE = 0;

    private int id;
    private String name;
    private State state;
    private int nextSpot;
    private int currentPlayerId;

    private Queue<Entry> queue = new LinkedList<>();
    private Map<Integer, Entry> userMap = new HashMap<>();

    public enum State {
        IDLE,
        PLAYING,
        CLOSED,
    }

    public GameQueue(final int id, final String name, final State state, final int nextSpot,
                     final int currentPlayerId) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.nextSpot = nextSpot;
        this.currentPlayerId = currentPlayerId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

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

    public int getCurrentPlayerId() {
        return currentPlayerId;
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

    public boolean contains(final int userId) {
        return currentPlayerId == userId || userMap.containsKey(userId);
    }

    private static final class Entry {
        private int userId;
        private int spot;

        public Entry(final int userId, final int spot) {
            this.userId = userId;
            this.spot = spot;
        }

        public int getUserId() {
            return userId;
        }

        public int getSpot() {
            return spot;
        }
    }
}
