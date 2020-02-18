package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.ReactionPatternRow;
import com.ryan_mtg.servobot.data.models.ReactionRow;
import com.ryan_mtg.servobot.data.repositories.ReactionPatternRepository;
import com.ryan_mtg.servobot.data.repositories.ReactionRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.reaction.AlwaysReact;
import com.ryan_mtg.servobot.model.reaction.Pattern;
import com.ryan_mtg.servobot.model.reaction.Reaction;
import com.ryan_mtg.servobot.model.reaction.ReactionCommand;
import com.ryan_mtg.servobot.model.reaction.ReactionFilter;
import com.ryan_mtg.servobot.model.reaction.WatershedFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReactionSerializer {
    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private ReactionPatternRepository reactionPatternRepository;

    public Reaction createReaction(final ReactionRow reactionRow, final List<Pattern> patterns,
                                   final List<ReactionCommand> commands) throws BotErrorException {
        return new Reaction(reactionRow.getId(), reactionRow.getEmote(), reactionRow.isSecure(),
                getFilter(reactionRow.getFilter()), patterns, commands);
    }

    public void saveReaction(final int botHomeId, final Reaction reaction) {
        ReactionRow reactionRow = new ReactionRow();
        reactionRow.setId(reaction.getId());
        reactionRow.setBotHomeId(botHomeId);
        reactionRow.setEmote(reaction.getEmoteName());
        reactionRow.setSecure(reaction.isSecure());
        reactionRow.setFilter(reaction.getFilter().getType());
        reactionRepository.save(reactionRow);
        reaction.setId(reactionRow.getId());
    }

    public ReactionFilter getFilter(final int filterType) {
        switch (filterType) {
            case AlwaysReact.TYPE:
                return new AlwaysReact();
            case WatershedFilter.TYPE:
                return new WatershedFilter();
        }
        throw new IllegalArgumentException("Unsupported type: " + filterType);
    }

    public Pattern createPattern(final ReactionPatternRow reactionPatternRow) throws BotErrorException {
        return new Pattern(reactionPatternRow.getId(), reactionPatternRow.getPattern());
    }

    public void savePattern(final int reactionId, final Pattern pattern) {
        ReactionPatternRow reactionPatternRow = new ReactionPatternRow();
        reactionPatternRow.setId(pattern.getId());
        reactionPatternRow.setPattern(pattern.getPatternString());
        reactionPatternRow.setReactionId(reactionId);
        reactionPatternRepository.save(reactionPatternRow);
        pattern.setId(reactionPatternRow.getId());
    }
}
