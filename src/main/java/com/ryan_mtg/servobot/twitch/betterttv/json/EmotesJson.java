package com.ryan_mtg.servobot.twitch.betterttv.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmotesJson {
    List<EmoteJson> channelEmotes;
    List<EmoteJson> sharedEmotes;
}
