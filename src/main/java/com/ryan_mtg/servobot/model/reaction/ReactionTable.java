package com.ryan_mtg.servobot.model.reaction;

import com.ryan_mtg.servobot.error.UserError;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ReactionTable implements Iterable<Reaction> {
    @Getter
    private List<Reaction> reactions = new ArrayList<>();

    public void registerReaction(final Reaction reaction) {
        reactions.add(reaction);
    }

    @NotNull
    @Override
    public Iterator<Reaction> iterator() {
        return reactions.iterator();
    }

    public void setTimeZone(final String timeZone) {
        reactions.forEach(reaction -> reaction.getFilter().setTimeZone(timeZone));
    }

    public ReactionTableEdit addReaction(final Reaction reaction) {
        ReactionTableEdit reactionTableEdit = new ReactionTableEdit();
        registerReaction(reaction);
        reactionTableEdit.save(reaction);
        return reactionTableEdit;
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
            reaction.getPatterns().forEach(reactionTableEdit::delete);
            reactionTableEdit.delete(reaction);
        });
        reactionTableEdit.getDeletedReactions().forEach(reaction -> reactions.remove(reaction));
        return reactionTableEdit;
    }

    public ReactionTableEdit addPattern(final int reactionId, final String patternString) throws UserError {
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
            reaction.getPatterns().stream().filter(pattern -> pattern.getId() == patternId)
                    .forEach(patternsToDelete::add);

            for(Pattern pattern: patternsToDelete) {
                reaction.remove(pattern);
                reactionTableEdit.delete(pattern);
            }
        });
        return reactionTableEdit;
    }
}
