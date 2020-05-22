package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;

public class TierCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.TIER_COMMAND_TYPE;

    public TierCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        User sender = event.getSender();
        String tier = getTier(event);
        String text = String.format("Hello, %s, your friendship tier is %s.", sender.getName(), tier);
        MessageCommand.say(event, text);
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitTierCommand(this);
    }

    private String getTier(final MessageSentEvent event) {
        Home home = event.getHome();
        if (home.isStreamer(event.getSender())) {
            return "The Mighty Linguine!";
        }
        return home.getRole(event.getSender(), event.getMessage().getServiceType());
    }
}
