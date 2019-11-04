package com.ryan_mtg.servobot.discord.reaction;

public class AlwaysReact extends AbstractReactionFilter {
    public static final int TYPE = 1;

    @Override
    public boolean shouldReact() {
        return true;
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
