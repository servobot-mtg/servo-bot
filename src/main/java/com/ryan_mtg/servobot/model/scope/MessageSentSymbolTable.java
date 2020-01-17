package com.ryan_mtg.servobot.model.scope;

import com.ryan_mtg.servobot.events.MessageSentEvent;

public class MessageSentSymbolTable implements SymbolTable {
    private MessageSentEvent event;

    public MessageSentSymbolTable(final MessageSentEvent event) {
        this.event = event;
    }

    @Override
    public Object lookup(final String name) {
        switch (name) {
            case "sender":
                return event.getSender().getName();
            case "home":
                return event.getHome().getName();
        }
        return null;
    }
}
