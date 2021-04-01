package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.discord.model.DiscordServiceHome;
import com.ryan_mtg.servobot.events.BotHomeEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;
import lombok.Getter;
import lombok.Setter;

public abstract class DiscordBotHomeEvent extends DiscordEvent implements BotHomeEvent {
    private final BotHome botHome;

    @Getter @Setter
    private HomeEditor homeEditor;

    public DiscordBotHomeEvent(final BotHome botHome) {
        this.botHome = botHome;
    }

    @Override
    public DiscordServiceHome getServiceHome() {
        return (DiscordServiceHome) botHome.getServiceHome(DiscordService.TYPE);
    }

    @Override
    public int getHomeId() {
        return botHome.getId();
    }

    @Override
    public ServiceHome getServiceHome(final int serviceType) {
        return botHome.getServiceHome(serviceType);
    }
}
