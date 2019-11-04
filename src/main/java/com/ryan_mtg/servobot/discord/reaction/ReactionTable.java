package com.ryan_mtg.servobot.discord.reaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReactionTable implements Iterable<Reaction>{
    private List<Reaction> reactions = new ArrayList<>();

    public void registerReaction(final Reaction reaction) {
        reactions.add(reaction);
    }

    @NotNull
    @Override
    public Iterator<Reaction> iterator() {
        return reactions.iterator();
    }

    public List<Reaction> getReactions() {
        return reactions;
    }
}
