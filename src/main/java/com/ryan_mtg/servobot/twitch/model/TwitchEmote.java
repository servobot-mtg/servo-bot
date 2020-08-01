package com.ryan_mtg.servobot.twitch.model;

import com.ryan_mtg.servobot.model.Emote;
import lombok.Getter;

public class TwitchEmote implements Emote {
    @Getter
    private String name;

    @Getter
    private long id;

    public TwitchEmote(final String name, final long id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getMessageText() {
        return name;
    }

    @Override
    public String getImageUrl() {
        return String.format("https://static-cdn.jtvnw.net/emoticons/v1/%d/1.0", id);
    }
}