package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;

public class TierCommand extends MessageCommand {
    public static final int TYPE = 3;

    public TierCommand(final int id, final boolean secure, final Permission permission) {
        super(id, secure, permission);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) {
        User sender = event.getSender();
        String tier = getTier(event);
        String text = String.format("Hello, %s, your friendship tier is %s.", sender.getName(), tier);
        MessageCommand.say(event, text);
    }

    @Override
    public int getType() {
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
