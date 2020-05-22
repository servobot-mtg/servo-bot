package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractHomedEvent implements Event {
    @Getter @Setter
    private BotEditor botEditor;

    @Getter @Setter
    private HomeEditor homeEditor;

    private BotHome botHome;

    protected AbstractHomedEvent(final BotHome botHome) {
        this.botHome = botHome;
    }

    public abstract Home getHome();

    @Override
    public int getHomeId() {
        return botHome.getId();
    }

    @Override
    public ServiceHome getServiceHome(final int serviceType) {
        return botHome.getServiceHome(serviceType);
    }
}
