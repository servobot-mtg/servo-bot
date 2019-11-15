package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;

public class DiscordServiceHome implements ServiceHome {
    private long guildId;
    private DiscordService discordService;

    public DiscordServiceHome(final DiscordService discordService, final long guildId) {
        this.discordService = discordService;
        this.guildId = guildId;
    }

    @Override
    public int getServiceType() {
        return DiscordService.TYPE;
    }

    @Override
    public String getDescription() {
        return String.format("Guild  %s", getHome().getName());
    }

    @Override
    public Service getService() {
        return discordService;
    }

    @Override
    public Home getHome() {
        return discordService.getHome(guildId);
    }

    @Override
    public void start(final BotHome botHome) {
        discordService.setNickName(guildId, botHome.getName());
    }

    public long getGuildId() {
        return guildId;
    }
}
