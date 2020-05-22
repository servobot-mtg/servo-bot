package com.ryan_mtg.servobot.commands.jail;

import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;

public class JailUtility {
    public static boolean isInJail(final Home home, final User user, final String prisonRole) {
        return home.hasRole(user, prisonRole);
    }

    public static boolean isInAnyJail(final Home home, final User user, final String prisonRole) {
        return home.hasRole(user, prisonRole);
    }
}
