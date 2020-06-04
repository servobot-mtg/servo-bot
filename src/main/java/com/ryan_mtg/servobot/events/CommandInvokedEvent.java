package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.editors.BookTableEditor;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;

public interface CommandInvokedEvent extends MessageEvent, UserEvent {
    String getCommand();
    String getArguments();
    CommandTableEditor getCommandTableEditor();
    BookTableEditor getBookTableEditor();

    default User getUser() {
        return getSender();
    }
}
