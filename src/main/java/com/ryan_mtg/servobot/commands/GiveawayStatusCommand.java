package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Reward;

public class GiveawayStatusCommand extends MessageCommand {
    public static final int TYPE = 22;

    public GiveawayStatusCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Reward reward = event.getHomeEditor().getGiveaway();
        if (reward == null) {
            MessageCommand.say(event, "There is no giveaway running currently.");
        } else {
            String message =
                    String.format("There are %d minutes left to enter. Type !enter in the Discord.",
                            reward.getTimeLeft().toMinutes());
            MessageCommand.say(event, message);
        }
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitGiveawayStatusCommand(this);
    }
}
