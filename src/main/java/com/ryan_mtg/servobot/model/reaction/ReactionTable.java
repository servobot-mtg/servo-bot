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

    public Reaction secureReaction(final int reactionId, final boolean secure) {
        Reaction reaction =
                reactions.stream().filter(testReaction -> testReaction.getId() == reactionId).findFirst().get();
        reaction.setSecure(secure);
        return reaction;
    }

    public ReactionTableEdit deleteReaction(final int reactionId) {
        ReactionTableEdit reactionTableEdit = new ReactionTableEdit();
        reactions.stream().filter(reaction -> reaction.getId() == reactionId).forEach(reaction -> {
            reaction.getPatterns().stream().forEach(pattern -> {
                reactionTableEdit.delete(pattern);
            });
            reactionTableEdit.delete(reaction);
        });
        return reactionTableEdit;
    }

    public ReactionTableEdit deletePattern(final int reactionId, final int patternId) {
        ReactionTableEdit reactionTableEdit = new ReactionTableEdit();
        reactions.stream().filter(reaction -> reaction.getId() == reactionId).forEach(reaction -> {
            reaction.getPatterns().stream().filter(pattern -> pattern.getId() == patternId).forEach(pattern -> {
                reactionTableEdit.delete(pattern);
            });
        });
        return reactionTableEdit;
    }
}
