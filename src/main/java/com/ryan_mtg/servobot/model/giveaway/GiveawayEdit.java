package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.commands.CommandTableEdit;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GiveawayEdit {
    private CommandTableEdit commandTableEdit = new CommandTableEdit();
    private Map<Giveaway, Integer> savedGiveaways = new HashMap<>();
    private Map<Prize, Integer> savedPrizes = new IdentityHashMap<>();
    private List<Prize> deletedPrizes = new ArrayList<>();

    public void addGiveaway(final int botHomeId, final Giveaway giveaway) {
        savedGiveaways.put(giveaway, botHomeId);
    }

    public void savePrize(final int giveawayId, final Prize prize) {
        savedPrizes.put(prize, giveawayId);
    }

    public void deletePrize(final Prize prize) {
        deletedPrizes.add(prize);
    }

    public void merge(final GiveawayEdit giveawayEdit) {
        commandTableEdit.merge(giveawayEdit.commandTableEdit);
        savedGiveaways.putAll(giveawayEdit.savedGiveaways);
        savedPrizes.putAll(giveawayEdit.savedPrizes);
        deletedPrizes.addAll(deletedPrizes);
    }

    public void merge(final CommandTableEdit commandTableEdit) {
        this.commandTableEdit.merge(commandTableEdit);
    }
}
