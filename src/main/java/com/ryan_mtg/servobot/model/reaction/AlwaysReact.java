package com.ryan_mtg.servobot.model.reaction;

import com.ryan_mtg.servobot.model.User;

public class AlwaysReact implements ReactionFilter {
    public static final int TYPE = 1;

    @Override
    public boolean shouldReact(final User sender) {
        return true;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void setTimeZone(final String timeZone) {}
}
