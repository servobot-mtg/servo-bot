package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.ReactionPatternRow;
import com.ryan_mtg.servobot.data.models.ReactionRow;
import com.ryan_mtg.servobot.data.repositories.ReactionPatternRepository;
import com.ryan_mtg.servobot.data.repositories.ReactionRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.reaction.Pattern;
import com.ryan_mtg.servobot.model.reaction.Reaction;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.reaction.ReactionTableEdit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ReactionTableSerializer {
    @Autowired
    private ReactionSerializer reactionSerializer;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private ReactionPatternRepository reactionPatternRepository;

    public ReactionTable createReactionTable(final int botHomeId) throws BotErrorException {
        Iterable<ReactionRow> reactionRows = reactionRepository.findAllByBotHomeId(botHomeId);
        ReactionTable reactionTable = new ReactionTable();

        for (ReactionRow reactionRow : reactionRows) {
            Reaction reaction = reactionSerializer.createReaction(reactionRow);

            Iterable<ReactionPatternRow> patterns = reactionPatternRepository.findAllByReactionId(reactionRow.getId());

            for (ReactionPatternRow pattern : patterns) {
                reaction.addPattern(reactionSerializer.createPattern(pattern));
            }

            reactionTable.registerReaction(reaction);
        }

        return reactionTable;
    }

    public void saveReactionTable(final int botHomeId, final ReactionTable reactionTable) {
        List<Reaction> reactions = reactionTable.getReactions();
        for (Reaction reaction : reactions) {
            reactionSerializer.saveReaction(botHomeId, reaction);
            for (Pattern pattern : reaction.getPatterns()) {
                reactionSerializer.savePattern(reaction.getId(), pattern);
            }
        }
    }

    public void commit(final int botHomeId, final ReactionTableEdit reactionTableEdit) {
        for (Pattern pattern : reactionTableEdit.getDeletedPatterns()) {
            reactionPatternRepository.deleteById(pattern.getId());
        }

        for (Reaction reaction : reactionTableEdit.getDeletedReactions()) {
            reactionRepository.deleteById(reaction.getId());
        }

        for (Reaction reaction : reactionTableEdit.getSavedReactions()) {
            reactionSerializer.saveReaction(botHomeId, reaction);
        }

        for (Map.Entry<Pattern, Integer> patternEntry : reactionTableEdit.getSavedPatterns().entrySet()) {
            reactionSerializer.savePattern(patternEntry.getValue(), patternEntry.getKey());
        }
    }
}
