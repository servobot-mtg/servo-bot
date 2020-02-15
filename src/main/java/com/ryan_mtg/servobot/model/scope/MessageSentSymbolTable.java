package com.ryan_mtg.servobot.model.scope;

import com.ryan_mtg.servobot.events.MessageSentEvent;

public class MessageSentSymbolTable implements SymbolTable {
    private MessageSentEvent event;
    private String input;

    public MessageSentSymbolTable(final MessageSentEvent event, final String input) {
        this.event = event;
        this.input = input;
    }

    @Override
    public Object lookup(final String name) {
        switch (name) {
            case "sender":
                return event.getSender().getName();
            case "home":
                return event.getHome().getName();
            case "input":
                return input;
        }
        return null;
    }
}
