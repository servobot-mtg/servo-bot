package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Reward;

public class StartGiveawayCommand extends MessageCommand {
    public static final int TYPE = 20;

    public StartGiveawayCommand(final int id, final boolean secure, final Permission permission) {
        super(id, secure, permission);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Reward reward = event.getHomeEditor().startGiveaway();
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
