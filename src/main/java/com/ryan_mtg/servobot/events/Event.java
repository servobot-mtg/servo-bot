package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;

public interface Event {
    int getHomeId();
    ServiceHome getServiceHome(int serviceType);

    BotEditor getBotEditor();
    void setBotEditor(BotEditor botEditor);
    HomeEditor getHomeEditor();
    void setHomeEditor(HomeEditor homeEditor);
}
