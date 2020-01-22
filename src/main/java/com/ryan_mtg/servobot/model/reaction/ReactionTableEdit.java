package com.ryan_mtg.servobot.model.reaction;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ReactionTableEdit {
    private List<Reaction> deletedReactions = new ArrayList<>();
    private List<Reaction> savedReactions = new ArrayList<>();

    private List<Pattern> deletedPatterns = new ArrayList<>();
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

    public List<Reaction> getDeletedReactions() {
        return deletedReactions;
    }

    public List<Reaction> getSavedReactions() {
        return savedReactions;
    }

    public Map<Pattern, Integer> getSavedPatterns() {
        return savedPatterns;
    }

    public List<Pattern> getDeletedPatterns() {
        return deletedPatterns;
    }
}
