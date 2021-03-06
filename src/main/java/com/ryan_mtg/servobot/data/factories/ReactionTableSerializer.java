package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.data.models.ReactionCommandRow;
import com.ryan_mtg.servobot.data.models.ReactionPatternRow;
import com.ryan_mtg.servobot.data.models.ReactionRow;
import com.ryan_mtg.servobot.data.repositories.ReactionCommandRepository;
import com.ryan_mtg.servobot.data.repositories.ReactionPatternRepository;
import com.ryan_mtg.servobot.data.repositories.ReactionRepository;
import com.ryan_mtg.servobot.model.reaction.Pattern;
import com.ryan_mtg.servobot.model.reaction.Reaction;
import com.ryan_mtg.servobot.model.reaction.ReactionCommand;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.reaction.ReactionTableEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ReactionTableSerializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactionTableSerializer.class);

    private final ReactionSerializer reactionSerializer;
    private final ReactionRepository reactionRepository;
    private final ReactionCommandRepository reactionCommandRepository;
    private final ReactionPatternRepository reactionPatternRepository;

    public ReactionTableSerializer(final ReactionSerializer reactionSerializer,
            final ReactionRepository reactionRepository, final ReactionCommandRepository reactionCommandRepository,
            final ReactionPatternRepository reactionPatternRepository) {
        this.reactionSerializer = reactionSerializer;
        this.reactionRepository = reactionRepository;
        this.reactionCommandRepository = reactionCommandRepository;
        this.reactionPatternRepository = reactionPatternRepository;
    }

    public ReactionTable createReactionTable(final int botHomeId, final CommandTable commandTable) {
        ReactionTable reactionTable = new ReactionTable();
        Iterable<ReactionRow> reactionRows = reactionRepository.findAllByBotHomeId(botHomeId);

        Iterable<Integer> reactionIds = SerializationSupport.getIds(reactionRows, ReactionRow::getId);

        Map<Integer, List<ReactionPatternRow>> patternRowMap = SerializationSupport.getIdMapping(
                reactionPatternRepository.findAllByReactionIdIn(reactionIds), reactionIds,
                ReactionPatternRow::getReactionId);

        Map<Integer, List<ReactionCommandRow>> commandRowMap = SerializationSupport.getIdMapping(
                reactionCommandRepository.findAllByReactionIdIn(reactionIds), reactionIds,
                ReactionCommandRow::getReactionId);

        for (ReactionRow reactionRow : reactionRows) {
            List<Pattern> patterns = new ArrayList<>();
            for (ReactionPatternRow pattern : patternRowMap.get(reactionRow.getId())) {
                patterns.add(reactionSerializer.createPattern(pattern));
            }

            List<ReactionCommand> commands = new ArrayList<>();
            for (ReactionCommandRow reactionCommandRow : commandRowMap.get(reactionRow.getId())) {
                Command command = commandTable.getCommand(reactionCommandRow.getCommandId());
                commands.add(new ReactionCommand(reactionCommandRow.getId(), command));
            }

            Reaction reaction = reactionSerializer.createReaction(reactionRow, patterns, commands);
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
