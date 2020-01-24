package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.user.HomedUser;

public class Entrant {
    private HomedUser user;

    public Entrant(final HomedUser user) {
        this.user = user;
    }

    public HomedUser getUser() {
        return user;
    }
}
