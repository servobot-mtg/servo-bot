package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;

public class DiscordSavedMessage implements Message {
    private DiscordServiceHome serviceHome;
    private DiscordMessage message;
    private long channelId;
    private long messageId;

    public DiscordSavedMessage(final DiscordServiceHome serviceHome, final long channelId, final long messageId) {
        this.serviceHome = serviceHome;
        this.channelId = channelId;
        this.messageId = messageId;
    }

    @Override
    public long getId() {
        return messageId;
    }

    @Override
    public long getChannelId() {
        return channelId;
    }

    @Override
    public String getContent() {
        return resolveMessage().getContent();
    }

    @Override
    public User getSender() {
        return resolveMessage().getSender();
    }

    @Override
    public boolean canEmote() {
        return resolveMessage().canEmote();
    }

    @Override
    public void addEmote(final Emote emote) {
        resolveMessage().addEmote(emote);
    }

    private Message resolveMessage() {
        if (message != null) {
            return message;
        }
        return message = serviceHome.getMessage(channelId, messageId);
    }
}
