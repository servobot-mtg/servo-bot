package com.ryan_mtg.servobot.discord.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DiscordUserJson {
    private String id;
    private String username;
}
