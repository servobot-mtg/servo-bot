package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.ReactionPatternRow;
import com.ryan_mtg.servobot.data.models.ReactionRow;
import com.ryan_mtg.servobot.data.repositories.ReactionPatternRepository;
import com.ryan_mtg.servobot.data.repositories.ReactionRepository;
import com.ryan_mtg.servobot.reaction.Reaction;
import com.ryan_mtg.servobot.reaction.ReactionTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReactionTableSerializer {
    @Autowired
    private ReactionSerializer reactionSerializer;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private ReactionPatternRepository reactionPatternRepository;

    public ReactionTable createReactionTable(final int botHomeId) {
        Iterable<ReactionRow> reactionRows = reactionRepository.findAllByBotHomeId(botHomeId);
        ReactionTable reactionTable = new ReactionTable();

        for (ReactionRow reactionRow : reactionRows) {
            Reaction reaction = reactionSerializer.createReaction(reactionRow);

            Iterable<ReactionPatternRow> patterns = reactionPatternRepository.findAllByReactionId(reactionRow.getId());

            for (ReactionPatternRow pattern : patterns) {
                reaction.addPattern(pattern.getPattern());
            }

            reactionTable.registerReaction(reaction);
        }

        return reactionTable;
    }

    public void saveReactionTable(final ReactionTable reactionTable, final int botHomeId) {
        List<Reaction> reactions = reactionTable.getReactions();
        for (Reaction reaction : reactions) {
            ReactionRow reactionRow = reactionSerializer.saveReaction(botHomeId, reaction);
            for (String pattern : reaction.getPatterns()) {
                ReactionPatternRow reactionPatternRow = new ReactionPatternRow();
                reactionPatternRow.setPattern(pattern);
                reactionPatternRow.setReactionId(reactionRow.getId());
                reactionPatternRepository.save(reactionPatternRow);
            }
        }
    }
}
