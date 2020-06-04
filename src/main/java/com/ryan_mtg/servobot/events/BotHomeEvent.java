package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.editors.StorageValueEditor;

public interface BotHomeEvent extends Event {
    int getHomeId();
    ServiceHome getServiceHome(int serviceType);

    HomeEditor getHomeEditor();
    void setHomeEditor(HomeEditor homeEditor);

    default StorageValueEditor getStorageValueEditor() {
        return getHomeEditor().getStorageValueEditor();
    }
}
