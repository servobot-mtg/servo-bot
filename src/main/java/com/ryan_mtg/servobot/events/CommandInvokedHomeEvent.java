package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.editors.GameQueueEditor;

public interface CommandInvokedHomeEvent extends CommandInvokedEvent, MessageHomeEvent {
    GameQueueEditor getGameQueueEditor();
}
