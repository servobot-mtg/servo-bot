package com.ryan_mtg.servobot.commands.jail;

import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;

public class JailUtility {
    public static boolean isInJail(final ServiceHome serviceHome, final User user, final String prisonRole) {
        return serviceHome.hasRole(user, prisonRole);
    }

    public static boolean isInAnyJail(final ServiceHome serviceHome, final User user, final String prisonRole) {
        return serviceHome.hasRole(user, prisonRole);
    }
}
