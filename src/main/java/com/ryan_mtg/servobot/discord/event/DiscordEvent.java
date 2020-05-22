package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.events.Event;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;
import lombok.Getter;
import lombok.Setter;

public abstract class DiscordEvent implements Event {
    private BotHome botHome;

    @Getter @Setter
    private BotEditor botEditor;

    @Getter @Setter
    private HomeEditor homeEditor;

    public DiscordEvent(final BotHome botHome) {
        this.botHome = botHome;
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
