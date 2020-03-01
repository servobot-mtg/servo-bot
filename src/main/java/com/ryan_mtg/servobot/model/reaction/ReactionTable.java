package com.ryan_mtg.servobot.model.reaction;

import com.ryan_mtg.servobot.events.BotErrorException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    public ReactionTableEdit addPattern(final int reactionId, final String patternString) throws BotErrorException {
        ReactionTableEdit reactionTableEdit = new ReactionTableEdit();
        for(Reaction reaction : reactions) {
            if (reaction.getId() == reactionId) {
                Pattern pattern = new Pattern(Pattern.UNREGISTERED_ID, patternString);
                reaction.addPattern(pattern);
                reactionTableEdit.save(reactionId, pattern);
            }
        }
        return reactionTableEdit;
    }

    public ReactionTableEdit deletePattern(final int reactionId, final int patternId) {
        ReactionTableEdit reactionTableEdit = new ReactionTableEdit();
        reactions.stream().filter(reaction -> reaction.getId() == reactionId).forEach(reaction -> {
            Set<Pattern> patternsToDelete = new HashSet<>();
            reaction.getPatterns().stream().filter(pattern -> pattern.getId() == patternId).forEach(pattern -> {
                patternsToDelete.add(pattern);
            });

            for(Pattern pattern: patternsToDelete) {
                reaction.remove(pattern);
                reactionTableEdit.delete(pattern);
            }
        });
        return reactionTableEdit;
    }
}
