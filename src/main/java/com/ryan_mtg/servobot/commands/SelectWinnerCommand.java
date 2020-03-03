package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.HomeEvent;
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
    public void perform(final HomeEvent homeEvent) throws BotErrorException {
        HomeEditor homeEditor = homeEvent.getHomeEditor();
        homeEditor.selectRaffleWinner(giveawayId);
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
