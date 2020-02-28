package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.HomeEditor;

public class SelectWinnerCommand extends HomeCommand {
    public static final int TYPE = 23;

    public SelectWinnerCommand(final int id, final int flags, final Permission permission) {
        super(id, flags, permission);
    }

    @Override
    public void perform(final HomeEvent homeEvent) {
        try {
            HomeEditor homeEditor = homeEvent.getHomeEditor();
            homeEditor.awardReward(homeEvent.getHomeEditor().getGiveaway().getId());
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
