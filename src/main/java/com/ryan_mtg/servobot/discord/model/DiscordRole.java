package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Role;
import lombok.Getter;

@Getter
public class DiscordRole implements Role {
    private long id;
    private String name;

    public DiscordRole(final net.dv8tion.jda.api.entities.Role role) {
        this.id = role.getIdLong();
        this.name = role.getName();
    }
}
