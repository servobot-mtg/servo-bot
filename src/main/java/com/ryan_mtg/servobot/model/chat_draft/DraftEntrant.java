package com.ryan_mtg.servobot.model.chat_draft;

import com.ryan_mtg.servobot.user.HomedUser;

public class DraftEntrant {
    public static final int UNREGISTERED_ID = 0;

    private int id;
    private HomedUser user;

    public DraftEntrant(final int id, final HomedUser user) {
        this.id = id;
        this.user = user;
    }
}
