package com.ryan_mtg.servobot.model.game_queue;

public enum PlayerState {
    RSVPED,
    RSVP_EXPIRED,
    WAITING,
    ON_CALL,
    READY,
    ON_DECK,
    NO_SHOW,
    PERMANENT,
    PLAYING,
    LG;

    public boolean isPlaying() {
        return this == LG || this == PERMANENT || this == PLAYING;
    }

    public boolean isActivelyPlaying() {
        return this == PERMANENT || this == PLAYING;
    }

    public boolean isOnDeck() {
        return this == READY || this == ON_DECK || this == NO_SHOW;
    }

    public boolean isWaiting() {
        return this == WAITING || this == ON_CALL;
    }
}
