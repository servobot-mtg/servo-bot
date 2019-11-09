package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;

public class DiscordMessage implements Message {
    private net.dv8tion.jda.api.entities.Message message;

    public DiscordMessage(final net.dv8tion.jda.api.entities.Message message) {
        this.message = message;
    }

    @Override
    public String getContent() {
        return message.getContentDisplay();
    }

    @Override
    public Channel getChannel() {
        return new DiscordChannel(new DiscordHome(message.getGuild()), message.getChannel());
    }

    @Override
    public Home getHome() {
        return new DiscordHome(message.getGuild());
    }

    @Override
    public User getSender() {
        return new DiscordUser(message.getMember());
    }

    @Override
    public boolean canEmote() {
        return true;
    }

    @Override
    public void addEmote(final Emote emote) {
        message.addReaction(((DiscordEmote)emote).getDiscordEmote()).queue();
    }
}
