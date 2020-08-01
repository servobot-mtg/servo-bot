package com.ryan_mtg.servobot.twitch.twitchemotes.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Emote {
    private String code;

    private long id;

    @JsonProperty("emoticon_set")
    private int emoticonSet;
}
