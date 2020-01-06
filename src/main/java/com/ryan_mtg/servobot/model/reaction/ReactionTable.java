package com.ryan_mtg.servobot.model.reaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReactionTable implements Iterable<Reaction> {
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

    public void setTimeZone(final String timeZone) {
        reactions.stream().forEach(reaction -> reaction.getFilter().setTimeZone(timeZone));
    }

    public Reaction secureReaction(int reactionId, boolean secure) {
        Reaction reaction =
                reactions.stream().filter(testReaction -> testReaction.getId() == reactionId).findFirst().get();
        reaction.setSecure(secure);
        return reaction;
    }
}
