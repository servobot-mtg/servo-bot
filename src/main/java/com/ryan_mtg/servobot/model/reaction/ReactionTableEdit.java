package com.ryan_mtg.servobot.model.reaction;

import lombok.Getter;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ReactionTableEdit {
    @Getter
    private List<Reaction> deletedReactions = new ArrayList<>();

    @Getter
    private List<Reaction> savedReactions = new ArrayList<>();

    @Getter
    private List<Pattern> deletedPatterns = new ArrayList<>();

    @Getter
    private Map<Pattern, Integer> savedPatterns = new IdentityHashMap<>();

    public void delete(final Reaction reaction) {
        deletedReactions.add(reaction);
    }

    public void save(final Reaction reaction) {
        savedReactions.add(reaction);
    }

    public void delete(final Pattern pattern) {
        deletedPatterns.add(pattern);
    }

    public void save(final int reactionId, final Pattern pattern) {
        savedPatterns.put(pattern, reactionId);
    }
}
