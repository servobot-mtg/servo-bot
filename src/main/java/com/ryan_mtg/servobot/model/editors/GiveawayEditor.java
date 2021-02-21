package com.ryan_mtg.servobot.model.editors;

import com.ryan_mtg.servobot.data.factories.GiveawaySerializer;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.LibraryError;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.GiveawayEdit;
import com.ryan_mtg.servobot.model.giveaway.Prize;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.HomedUserTable;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GiveawayEditor {
    private final int contextId;
    private final List<Giveaway> giveaways;
    private final GiveawaySerializer giveawaySerializer;
    private final HomedUserTable userTable;

    public Prize awardPrize(final int giveawayId, final int prizeId, final Integer winnerId)
            throws BotHomeError, LibraryError {
        HomedUser winner = winnerId != null ? userTable.getById(winnerId) : null;
        GiveawayEdit giveawayEdit = getGiveaway(giveawayId).awardPrize(prizeId, winner);
        giveawaySerializer.commit(giveawayEdit);
        return getSavedPrize(giveawayEdit);
    }

    public Prize reservePrize(final int giveawayId, final int prizeId) throws LibraryError {
        GiveawayEdit giveawayEdit = getGiveaway(giveawayId).reservePrize(prizeId);
        giveawaySerializer.commit(giveawayEdit);
        return getSavedPrize(giveawayEdit);
    }

    public Prize releasePrize(final int giveawayId, final int prizeId) throws LibraryError {
        GiveawayEdit giveawayEdit = getGiveaway(giveawayId).releasePrize(prizeId);
        giveawaySerializer.commit(giveawayEdit);
        return getSavedPrize(giveawayEdit);
    }

    public Prize bestowPrize(int giveawayId, int prizeId) throws LibraryError {
        GiveawayEdit giveawayEdit = getGiveaway(giveawayId).bestowPrize(prizeId);
        giveawaySerializer.commit(giveawayEdit);
        return getSavedPrize(giveawayEdit);
    }

    public Prize requestPrize(final int giveawayId, final HomedUser requester) throws BotHomeError, UserError {
        GiveawayEdit giveawayEdit = getGiveaway(giveawayId).requestPrize(requester);
        Prize prize = giveawayEdit.getSavedPrizes().keySet().iterator().next();
        giveawaySerializer.commit(giveawayEdit);

        return prize;
    }

    public void deletePrize(final int giveawayId, final int prizeId) throws LibraryError {
        GiveawayEdit giveawayEdit = getGiveaway(giveawayId).deletePrize(prizeId);
        giveawaySerializer.commit(giveawayEdit);
    }

    private Giveaway getGiveaway(final int giveawayId) {
        return giveaways.stream().filter(g -> g.getId() == giveawayId).findFirst()
                .orElseThrow(() -> new SystemError("Giveaway %d not found ", giveawayId));
    }

    private Prize getSavedPrize(final GiveawayEdit giveawayEdit) {
        return giveawayEdit.getSavedPrizes().keySet().stream().findFirst().get();
    }
}
