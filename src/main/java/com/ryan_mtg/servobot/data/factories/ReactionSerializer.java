package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.ReactionRow;
import com.ryan_mtg.servobot.data.repositories.ReactionRepository;
import com.ryan_mtg.servobot.reaction.AlwaysReact;
import com.ryan_mtg.servobot.reaction.Reaction;
import com.ryan_mtg.servobot.reaction.ReactionFilter;
import com.ryan_mtg.servobot.reaction.WatershedFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReactionSerializer {
    @Autowired
    private ReactionRepository reactionRepository;

    public Reaction createReaction(final ReactionRow reactionRow) {
        return new Reaction(reactionRow.getId(), reactionRow.getEmote(), reactionRow.isSecure(),
                getFilter(reactionRow.getFilter()));
    }

    public ReactionRow saveReaction(final int botHomeId, final Reaction reaction) {
        ReactionRow reactionRow = new ReactionRow();
        reactionRow.setId(reaction.getId());
        reactionRow.setBotHomeId(botHomeId);
        reactionRow.setEmote(reaction.getEmoteName());
        reactionRow.setSecure(reaction.isSecure());
        reactionRow.setFilter(reaction.getFilter().getType());
        reactionRepository.save(reactionRow);
        return reactionRow;
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
}
