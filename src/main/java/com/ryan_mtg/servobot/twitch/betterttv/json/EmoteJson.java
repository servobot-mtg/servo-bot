package com.ryan_mtg.servobot.twitch.betterttv.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmoteJson {
    private String code;

    private String id;

    private String imageType;
}