package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractHomedEvent implements Event {
    @Getter @Setter
    private BotEditor botEditor;

    @Getter @Setter
    private HomeEditor homeEditor;

    @Getter
    private int homeId;

    protected AbstractHomedEvent(final int homeId) {
        this.homeId = homeId;
    }

    public abstract Home getHome();
}
