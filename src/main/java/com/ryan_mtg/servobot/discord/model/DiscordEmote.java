package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Emote;

public class DiscordEmote implements Emote {
    private net.dv8tion.jda.api.entities.Emote emote;

    public DiscordEmote(final net.dv8tion.jda.api.entities.Emote emote) {
        this.emote = emote;
    }

    public net.dv8tion.jda.api.entities.Emote getDiscordEmote() {
        return emote;
    }
}
