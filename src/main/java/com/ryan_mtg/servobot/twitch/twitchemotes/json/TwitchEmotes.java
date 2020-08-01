package com.ryan_mtg.servobot.twitch.twitchemotes.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TwitchEmotes {
    @JsonProperty("channel_name")
    private String channelName;

    @JsonProperty("channel_id")
    private int channelId;

    @JsonProperty("broadcaster_type")
    private String broadcasterType;

    List<Emote> emotes;
}
