package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.commands.CommandTableEdit;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class GiveawayEdit {
    private CommandTableEdit commandTableEdit = new CommandTableEdit();
    private List<Giveaway> savedGiveaways = new ArrayList<>();
    private Map<Prize, Integer> savedPrizes = new IdentityHashMap<>();

    public void addGiveaway(final Giveaway giveaway) {
        savedGiveaways.add(giveaway);
    }

    public List<Giveaway> getSavedGiveaways() {
        return savedGiveaways;
    }

    public void addPrize(final int giveawayId, final Prize prize) {
        savedPrizes.put(prize, giveawayId);
    }

    public Map<Prize, Integer> getSavedPrizes() {
        return savedPrizes;
    }

    public void merge(final CommandTableEdit commandTableEdit) {
        this.commandTableEdit.merge(commandTableEdit);
    }

    public CommandTableEdit getCommandTableEdit() {
        return commandTableEdit;
    }
}
