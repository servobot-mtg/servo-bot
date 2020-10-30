package com.ryan_mtg.servobot.model.game_queue;

public enum PlayerState {
    RSVPED,
    RSVP_EXPIRED,
    WAITING,
    ON_DECK,
    PERMANENT,
    PLAYING,
    LG;

    public boolean isPlaying() {
        return this == LG || this == PERMANENT || this == PLAYING;
    }
}
