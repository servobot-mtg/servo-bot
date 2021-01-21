package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.Application;
import com.ryan_mtg.servobot.error.SystemError;
import lombok.Getter;

@Getter
public enum Game {
    ARENA(1, "MTG Arena", 1, 1, new ArenaBehavior()),
    AMONG_US(2, "Among Us", Application.isTesting() ? 2 : 9, Application.isTesting() ? 3 :10, new AmongUsBehavior()),
    BATTLEGROUNDS(3, "Battlegrounds", 2, 4, new BattleGroundsBehavior());

    private final int type;
    private final String name;
    private final int minPlayers;
    private final int maxPlayers;
    private final GameBehavior gameBehavior;

    Game(final int type, final String name, final int minPlayers, final int maxPlayers,
            final GameBehavior gameBehavior) {
        this.type = type;
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.gameBehavior = gameBehavior;
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
