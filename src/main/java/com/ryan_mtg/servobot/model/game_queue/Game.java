package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.error.SystemError;
import lombok.Getter;

@Getter
public enum Game {
    ARENA(1, "MTG Arena"),
    AMONG_US(2, "Among Us");

    private final int type;
    private final String name;

    Game(final int type, final String name) {
        this.type = type;
        this.name = name;
    }

    public static Game get(final int type) {
        for (Game value : Game.values()) {
            if (value.getType() == type) {
                return value;
            }
        }
        throw new SystemError("Unknown type: " + type);
    }
}
