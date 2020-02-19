package com.ryan_mtg.servobot.model.reaction;

import com.ryan_mtg.servobot.model.User;

public class UserReactionFilter implements ReactionFilter {
    public static final int TYPE = 3;

    private final int userId;

    public UserReactionFilter(final int userId) {
        this.userId = userId;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void setTimeZone(final String timeZone) {}

    @Override
    public boolean shouldReact(final User sender) {
        return sender.getHomedUser().getId() == userId;
    }
}
