package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;

import java.util.List;

public class DiscordServiceHome implements ServiceHome {
    private long guildId;
    private DiscordService discordService;
    private HomeEditor homeEditor;

    public DiscordServiceHome(final DiscordService discordService, final long guildId) {
        this.discordService = discordService;
        this.guildId = guildId;
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
    public int getServiceType() {
        return DiscordService.TYPE;
    }

    @Override
    public String getDescription() {
        return String.format("Guild  %s", getHome().getName());
    }

    @Override
    public String getLink() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public List<String> getEmotes() {
        return discordService.getEmotes(guildId);
    }

    @Override
    public List<String> getRoles() {
        return discordService.getRoles(guildId);
    }

    @Override
    public List<String> getChannels() {
        return discordService.getChannels(guildId);
    }

    @Override
    public boolean isStreaming() {
        return discordService.isStreaming(guildId);
    }

    @Override
    public void start(final BotHome botHome) {
        setName(botHome.getBotName());
    }

    @Override
    public void stop(final BotHome botHome) {}

    @Override
    public void setHomeEditor(final HomeEditor homeEditor) {
        this.homeEditor = homeEditor;
    }

    @Override
    public void setName(String botName) {
        discordService.setNickName(guildId, botName);
    }

    public long getGuildId() {
        return guildId;
    }
}
