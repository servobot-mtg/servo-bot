package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.giveaway.Reward;

public class StartGiveawayCommand extends MessageCommand {
    public static final int TYPE = 20;
    private int giveawayId;

    public StartGiveawayCommand(final int id, final int flags, final Permission permission, final int giveawayId) {
        super(id, flags, permission);
        this.giveawayId = giveawayId;
    }

    public int getGiveawayId() {
        return giveawayId;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Reward reward = event.getHomeEditor().startGiveaway(giveawayId);
        String message =
                String.format("The giveaway has started! There are %d minutes left to type !enter in the Discord.",
                        reward.getTimeLeft().toMinutes());
        MessageCommand.say(event, message);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitStartGiveawayCommand(this);
    }
}
