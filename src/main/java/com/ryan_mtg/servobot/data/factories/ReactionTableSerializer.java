package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.data.models.ReactionCommandRow;
import com.ryan_mtg.servobot.data.models.ReactionPatternRow;
import com.ryan_mtg.servobot.data.models.ReactionRow;
import com.ryan_mtg.servobot.data.repositories.ReactionCommandRepository;
import com.ryan_mtg.servobot.data.repositories.ReactionPatternRepository;
import com.ryan_mtg.servobot.data.repositories.ReactionRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.reaction.Pattern;
import com.ryan_mtg.servobot.model.reaction.Reaction;
import com.ryan_mtg.servobot.model.reaction.ReactionCommand;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.reaction.ReactionTableEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class ReactionTableSerializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactionTableSerializer.class);

    @Autowired
    private ReactionSerializer reactionSerializer;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private ReactionCommandRepository reactionCommandRepository;

    @Autowired
    private ReactionPatternRepository reactionPatternRepository;

    public ReactionTable createReactionTable(final int botHomeId, final CommandTable commandTable)
            throws BotErrorException {
        ReactionTable reactionTable = new ReactionTable();
        Iterable<ReactionRow> reactionRows = reactionRepository.findAllByBotHomeId(botHomeId);

        List<Integer> reactionIds = StreamSupport.stream(reactionRows.spliterator(), false)
                .map(reactionRow -> reactionRow.getId()).collect(Collectors.toList());

        Map<Integer, List<ReactionPatternRow>> patternRowMap = new HashMap<>();
        reactionIds.forEach(reactionId -> patternRowMap.put(reactionId, new ArrayList<>()));
        for(ReactionPatternRow reactionPatternRow : reactionPatternRepository.findAllByReactionIdIn(reactionIds)) {
            patternRowMap.get(reactionPatternRow.getReactionId()).add(reactionPatternRow);
        }

        Map<Integer, List<ReactionCommandRow>> commandRowMap = new HashMap<>();
        reactionIds.forEach(reactionId -> commandRowMap.put(reactionId, new ArrayList<>()));
        for(ReactionCommandRow reactionCommandRow : reactionCommandRepository.findAllByReactionIdIn(reactionIds)) {
            commandRowMap.get(reactionCommandRow.getReactionId()).add(reactionCommandRow);
        }

        for (ReactionRow reactionRow : reactionRows) {
            List<Pattern> patterns = new ArrayList<>();
            LOGGER.info("Trying to read: {}", reactionRow.getId());
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
