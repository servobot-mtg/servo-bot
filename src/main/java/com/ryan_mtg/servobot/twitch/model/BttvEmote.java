package com.ryan_mtg.servobot.twitch.model;

import com.ryan_mtg.servobot.model.Emote;
import lombok.Getter;

public class BttvEmote implements Emote {
    @Getter
    private final String name;

    @Getter
    private final String id;

    public BttvEmote(final String name, final String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getMessageText() {
        return name;
    }

    @Override
    public String getImageUrl() {
        return String.format("https://cdn.betterttv.net/emote/%s/1x", id);
    }

    @Override
    public boolean isPermitted() {
        return true;
    }
}
