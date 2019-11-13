package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;

public class TierCommand extends MessageCommand {
    public static final int TYPE = 3;

    public TierCommand(final int id) {
        super(id);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return "Friendship Tier Command";
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitTierCommand(this);
    }

    @Override
    public void perform(final Message message, final String arguments) {
        User sender = message.getSender();
        String tier = getTier(message);
        String text = String.format("Hello %s, your friendship tier is %s.", sender.getName(), tier);
        MessageCommand.say(message, text);
    }

    private String getTier(final Message message) {
        Home home = message.getHome();
        if (home.isStreamer(message.getSender())) {
            return "The Mighty Linguine!";
        }
        return home.getRole(message.getSender(), message.getServiceType());
    }
}
