package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;

public class SelectWinnerCommand extends HomeCommand {
    public static final int TYPE = 23;
    private int giveawayId;

    public SelectWinnerCommand(final int id, final int flags, final Permission permission, final int giveawayId) {
        super(id, flags, permission);
        this.giveawayId = giveawayId;
    }

    public int getGiveawayId() {
        return giveawayId;
    }

    @Override
    public void perform(final Home home) {
        try {
            HomeEditor homeEditor = home.getHomeEditor();
            homeEditor.awardReward(giveawayId, homeEditor.getGiveaway(giveawayId).getId());
        } catch (BotErrorException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSelectWinnerCommand(this);
    }
}
