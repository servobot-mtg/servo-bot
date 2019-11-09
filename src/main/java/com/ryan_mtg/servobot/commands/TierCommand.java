package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

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

    @Override
    public void perform(final net.dv8tion.jda.api.entities.Message message, final String arguments) {
        Member member = message.getMember();
        String tier = getTier(message);
        String text = String.format("Hello %s, your friendship tier is %s.", member.getEffectiveName(), tier);
        MessageCommand.say(message, text);
    }

    private String getTier(final Message message) {
        Home home = message.getHome();
        if (home.isStreamer(message.getSender())) {
            return "The Mighty Linguine!";
        }
        return home.getRole(message.getSender());
    }

    private String getTier(final net.dv8tion.jda.api.entities.Message message) {
        List<Role> roles = message.getMember().getRoles();
        Member member = message.getMember();
        if (member.isOwner()) {
           return "The Mighty Linguine!";
        }
        for (Role role : roles) {
            return role.getName();
        }
        return "Pleb";
    }
}
