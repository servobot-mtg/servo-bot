package com.ryan_mtg.servobot.model.reaction;

public interface ReactionFilter {
    int getType();
    void setTimeZone(String timeZone);
    boolean shouldReact();
}
