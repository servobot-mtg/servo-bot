package com.ryan_mtg.servobot.model.reaction;

public class AlwaysReact implements ReactionFilter {
    public static final int TYPE = 1;

    @Override
    public boolean shouldReact() {
        return true;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void setTimeZone(final String timeZone) {}
}
