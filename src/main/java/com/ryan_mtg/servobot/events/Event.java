package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.HomeEditor;

public interface Event {
    int getHomeId();

    BotEditor getBotEditor();
    void setBotEditor(BotEditor botEditor);
    HomeEditor getHomeEditor();
    void setHomeEditor(HomeEditor homeEditor);
}
