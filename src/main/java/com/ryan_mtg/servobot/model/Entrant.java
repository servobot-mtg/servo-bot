package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;

public class Entrant {
    @Getter
    private HomedUser user;

    public Entrant(final HomedUser user) {
        this.user = user;
    }
}
