package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.commands.CommandTableEdit;
import lombok.Getter;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class GiveawayEdit {
    @Getter
    private CommandTableEdit commandTableEdit = new CommandTableEdit();

    @Getter
    private List<Giveaway> savedGiveaways = new ArrayList<>();

    @Getter
    private Map<Prize, Integer> savedPrizes = new IdentityHashMap<>();

    @Getter
    private List<Prize> deletedPrizes = new ArrayList<>();

    public void addGiveaway(final Giveaway giveaway) {
        savedGiveaways.add(giveaway);
    }

    public void addPrize(final int giveawayId, final Prize prize) {
        savedPrizes.put(prize, giveawayId);
    }

    public void merge(final CommandTableEdit commandTableEdit) {
        this.commandTableEdit.merge(commandTableEdit);
    }

    public void deletePrize(final Prize prize) {
        deletedPrizes.add(prize);
    }
}
