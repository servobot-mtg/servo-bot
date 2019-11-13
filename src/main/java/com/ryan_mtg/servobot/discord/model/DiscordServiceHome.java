package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.ServiceHome;

public class DiscordServiceHome implements ServiceHome {
    private long guildId;

    public DiscordServiceHome(final long guildId) {
        this.guildId = guildId;
    }

    @Override
    public int getServiceType() {
        return DiscordService.TYPE;
    }

    public long getGuildId() {
        return guildId;
    }
}
