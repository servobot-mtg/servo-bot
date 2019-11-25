package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.discord.event.DiscordMessageSentEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;

public class DiscordMessage implements Message {
    private DiscordMessageSentEvent event;
    private net.dv8tion.jda.api.entities.Message message;
    private HomeEditor homeEditor;

    public DiscordMessage(final DiscordMessageSentEvent event, final net.dv8tion.jda.api.entities.Message message, final HomeEditor homeEditor) {
        this.event = event;
        this.message = message;
        this.homeEditor = homeEditor;
    }

    @Override
    public String getContent() {
        return message.getContentDisplay();
    }

    @Override
    public Channel getChannel() {
        return new DiscordChannel(new DiscordHome(message.getGuild(), homeEditor), message.getChannel());
    }

    @Override
    public int getServiceType() {
        return DiscordService.TYPE;
    }

    @Override
    public Home getHome() {
        return new DiscordHome(message.getGuild(), homeEditor);
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
