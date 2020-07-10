package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;

public class TierCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.TIER_COMMAND_TYPE;

    public TierCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError {
        User sender = event.getSender();
        String tier = getTier(event);
        String text = String.format("Hello, %s, your friendship tier is %s.", sender.getName(), tier);
        event.say(text);
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitTierCommand(this);
    }

    private String getTier(final CommandInvokedHomeEvent event) {
        ServiceHome serviceHome = event.getServiceHome();
        if (serviceHome.isStreamer(event.getSender())) {
            return "The Mighty Linguine!";
        }
        return serviceHome.getRole(event.getSender());
    }
}
