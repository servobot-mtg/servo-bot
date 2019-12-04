package com.ryan_mtg.servobot.model;

public class GameQueueEntry {
    private int userId;
    private int spot;
    private int position;

    public GameQueueEntry(final int userId, final int spot, final int position) {
        this.userId = userId;
        this.spot = spot;
        this.position = position;
    }

    public int getUserId() {
        return userId;
    }

    public int getSpot() {
        return spot;
    }

    public int getPosition() {
        return position;
    }
}
