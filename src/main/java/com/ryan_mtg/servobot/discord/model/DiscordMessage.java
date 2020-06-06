package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.events.MessageEvent;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;

public class DiscordMessage implements Message {
    private MessageEvent event;
    private net.dv8tion.jda.api.entities.Message message;

    public DiscordMessage(final MessageEvent event, final net.dv8tion.jda.api.entities.Message message) {
        this.event = event;
        this.message = message;
    }

    @Override
    public String getContent() {
        return message.getContentDisplay();
    }

    @Override
    public User getSender() {
        return event.getSender();
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
