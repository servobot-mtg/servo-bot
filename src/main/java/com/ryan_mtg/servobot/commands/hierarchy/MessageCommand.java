package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.scope.SymbolTable;

public abstract class MessageCommand extends Command {

    abstract public void perform(MessageSentEvent event, String arguments) throws BotErrorException;

    public MessageCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    protected static Scope getMessageScope(final MessageSentEvent event) {
        HomeEditor homeEditor = event.getHomeEditor();
        SimpleSymbolTable messageSymbolTable = new SimpleSymbolTable();
        messageSymbolTable.addValue("sender", event.getSender().getName());
        messageSymbolTable.addValue("home", event.getHome().getName());

        return new Scope(homeEditor.getScope(), messageSymbolTable);
    }

    protected static void say(final MessageSentEvent event, final String text) throws BotErrorException {
        say(event, null, text);
    }

    protected static void say(final MessageSentEvent event, final SymbolTable commandSymbolTable, final String text)
            throws BotErrorException {
        Scope commandScope = getMessageScope(event);
        if (commandSymbolTable != null) {
            commandScope = new Scope(commandScope, commandSymbolTable);
        }
        Command.say(event.getChannel(), event, commandScope, text);
    }

    protected static void sayRaw(final MessageSentEvent event, final String text) {
        Channel channel = event.getChannel();
        channel.say(text);
    }

}
