package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.ReactionPatternRow;
import com.ryan_mtg.servobot.data.models.ReactionRow;
import com.ryan_mtg.servobot.data.repositories.ReactionPatternRepository;
import com.ryan_mtg.servobot.data.repositories.ReactionRepository;
import com.ryan_mtg.servobot.discord.reaction.Reaction;
import com.ryan_mtg.servobot.discord.reaction.ReactionTable;
import com.ryan_mtg.servobot.discord.reaction.WatershedFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("useDatabase")
    private boolean useDatabase;

    public ReactionTable createReactionTable(final int botHomeId) {
        if (useDatabase) {
            return createPersistedRegisteredTable(botHomeId);
        }
        return getMooseReactionTable();
    }

    public void saveReactionTable(final ReactionTable reactionTable, final int botHomeId) {
        List<Reaction> reactions = reactionTable.getReactions();
        for (Reaction reaction : reactions) {
            ReactionRow reactionRow = reactionSerializer.saveReaction(reaction, botHomeId);
            for (String pattern : reaction.getPatterns()) {
                ReactionPatternRow reactionPatternRow = new ReactionPatternRow();
                reactionPatternRow.setPattern(pattern);
                reactionPatternRow.setReactionId(reactionRow.getId());
                reactionPatternRepository.save(reactionPatternRow);
            }
        }
    }

    private ReactionTable createPersistedRegisteredTable(final int botHomeId) {
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


    private ReactionTable getMooseReactionTable() {
        ReactionTable reactionTable = new ReactionTable();
        reactionTable.registerReaction(new Reaction(Reaction.UNREGISTERED_ID, "OMG", "graze it", "sloth", "grazer"));
        reactionTable.registerReaction(new Reaction(Reaction.UNREGISTERED_ID, "LUL", "moose"));
        reactionTable.registerReaction(new Reaction(Reaction.UNREGISTERED_ID, "Frank", "frank"));
        reactionTable.registerReaction(new Reaction(Reaction.UNREGISTERED_ID, "pccb", "pumpkin"));
        reactionTable.registerReaction(new Reaction(Reaction.UNREGISTERED_ID, "fast", "@fast"));
        reactionTable.registerReaction(new Reaction(Reaction.UNREGISTERED_ID, "PG", new WatershedFilter(),
                "!fuck", "@ass", "crap", "@hell", "!cunt", "shit", "!dick", "wanker", "!bitch", "damn",
                "!nigger", "slut", "!twat", "!cock", "@trump", "assh"));
        return reactionTable;
    }
}
