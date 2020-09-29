package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;

public class DiscordMessage implements Message {
    private User sender;
    private net.dv8tion.jda.api.entities.Message message;

    public DiscordMessage(final User sender, final net.dv8tion.jda.api.entities.Message message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public long getId() {
        return message.getIdLong();
    }

    @Override
    public long getChannelId() {
        return message.getChannel().getIdLong();
    }

    @Override
    public String getContent() {
        return message.getContentDisplay();
    }

    @Override
    public User getSender() {
        return sender;
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
