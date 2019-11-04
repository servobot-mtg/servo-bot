package com.ryan_mtg.servobot.discord.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
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
        Member member = message.getMember();
        String tier = getTier(message);
        String text = String.format("Hello %s, your friendship tier is %s.", member.getEffectiveName(), tier);
        MessageCommand.say(message, text);
    }

    private String getTier(final Message message) {
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
