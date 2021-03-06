package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.editors.ChatDraftEditor;
import com.ryan_mtg.servobot.model.editors.GameQueueEditor;
import com.ryan_mtg.servobot.model.editors.RoleTableEditor;
import com.ryan_mtg.servobot.model.editors.StorageValueEditor;

public interface BotHomeEvent extends Event {
    int getHomeId();
    ServiceHome getServiceHome();
    ServiceHome getServiceHome(int serviceType);

    HomeEditor getHomeEditor();
    void setHomeEditor(HomeEditor homeEditor);

    default RoleTableEditor getRoleTableEditor() {
        return getHomeEditor().getRoleTableEditor();
    }

    default GameQueueEditor getGameQueueEditor() {
        return getHomeEditor().getGameQueueEditor();
    }

    default StorageValueEditor getStorageValueEditor() {
        return getHomeEditor().getStorageValueEditor();
    }

    default ChatDraftEditor getChatDraftEditor() {
        return getHomeEditor().getChatDraftEditor();
    }
}
