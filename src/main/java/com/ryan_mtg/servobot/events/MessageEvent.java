package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.scope.SymbolTable;

import java.util.List;

public interface MessageEvent extends UserEvent {
    Channel getChannel();
    User getSender();
    Message getMessage();

    default User getUser() {
        return getSender();
    }

    default void say(final String text) throws BotHomeError {
        say(null, text);
    }

    default void sendImage(final String url, final String fileName, final String description) throws UserError {
        sendImage(getChannel(), url, fileName, description);
    }

    default void sendImages(final List<String> urls, final String fileName, final List<String> descriptions)
            throws UserError {
        sendImages(getChannel(), urls, fileName, descriptions);
    }

    default void say(final SymbolTable symbolTable, final String text) throws BotHomeError {
        Scope innerScope = getScope();
        if (symbolTable != null) {
            innerScope = new Scope(innerScope, symbolTable);
        }
        say(getChannel(), innerScope, text);
    }

    default void sayRaw(final String text) {
        getChannel().say(text);
    }
}
