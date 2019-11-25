package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.HomeEditor;

public interface Event {
    int getHomeId();

    HomeEditor getHomeEditor();
    void setHomeEditor(HomeEditor homeEditor);
}
