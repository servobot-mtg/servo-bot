package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;

public class DiscordServiceHome implements ServiceHome {
    private long guildId;
    private DiscordService discordService;
    private HomeEditor homeEditor;

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
        return discordService.getHome(guildId, homeEditor);
    }

    @Override
    public void start(final BotHome botHome) {
        discordService.setNickName(guildId, botHome.getName());
    }

    @Override
    public void setHomeEditor(final HomeEditor homeEditor) {
        this.homeEditor = homeEditor;
    }

    public long getGuildId() {
        return guildId;
    }
}
