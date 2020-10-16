package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.error.SystemError;
import lombok.Getter;

@Getter
public enum Game {
    ARENA(1, "MTG Arena", 1, 1),
    AMONG_US(2, "Among Us", 6, 10);

    private final int type;
    private final String name;
    private final int minPlayers;
    private final int maxPlayers;

    Game(final int type, final String name, final int minPlayers, final int maxPlayers) {
        this.type = type;
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
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
