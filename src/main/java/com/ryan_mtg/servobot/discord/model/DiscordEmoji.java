package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Emote;

public class DiscordEmoji implements Emote {
    private final String name;

    public DiscordEmoji(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMessageText() {
        return String.format("\\N{%s}", name);
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public boolean isPermitted() {
        return true;
    }
}
