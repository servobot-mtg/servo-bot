package com.ryan_mtg.servobot.model.reaction;

import com.ryan_mtg.servobot.model.User;

public interface ReactionFilter {
    int getType();
    void setTimeZone(String timeZone);
    boolean shouldReact(final User sender);
}
