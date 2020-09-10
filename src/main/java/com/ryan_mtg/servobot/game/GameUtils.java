package com.ryan_mtg.servobot.game;

import com.ryan_mtg.servobot.user.User;

public class GameUtils {
    public static boolean isSame(final User player, final User otherPlayer) {
        return player.getId() == otherPlayer.getId();
    }
}
