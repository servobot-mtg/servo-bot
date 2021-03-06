package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Emote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordEmote implements Emote {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordEmote.class);
    private final net.dv8tion.jda.api.entities.Emote emote;

    public DiscordEmote(final net.dv8tion.jda.api.entities.Emote emote) {
        this.emote = emote;
    }

    public net.dv8tion.jda.api.entities.Emote getDiscordEmote() {
        return emote;
    }

    @Override
    public String getName() {
        return emote.getName();
    }

    @Override
    public String getMessageText() {
        return emote.getAsMention();
    }

    @Override
    public String getImageUrl() {
        return emote.getImageUrl();
    }

    @Override
    public boolean isPermitted() {
        return emote.canInteract(emote.getGuild().getSelfMember());
    }
}
