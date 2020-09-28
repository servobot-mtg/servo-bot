package com.ryan_mtg.servobot.model.game_queue;

public enum Game {
    ARENA(1, "MTG Arena"),
    AMONG_US(2, "Among Us");

    private final int type;
    private final String name;

    Game(final int type, final String name) {
        this.type = type;
        this.name = name;
    }
}
