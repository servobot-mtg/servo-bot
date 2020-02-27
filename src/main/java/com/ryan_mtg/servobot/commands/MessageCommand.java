package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.Event;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.parser.ParseException;
import com.ryan_mtg.servobot.model.parser.Parser;
import com.ryan_mtg.servobot.model.scope.FunctorSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.scope.SymbolTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MessageCommand extends Command {
    private static Pattern REPLACEMENT_PATTERN = Pattern.compile("%([^%]*)%");

    abstract public void perform(MessageSentEvent event, String arguments) throws BotErrorException;

    public MessageCommand(final int id, final int flags, final Permission permission) {
        super(id, flags, permission);
    }

    protected static Scope getMessageScope(final MessageSentEvent event) {
        HomeEditor homeEditor = event.getHomeEditor();
        FunctorSymbolTable messageSymbolTable = new FunctorSymbolTable();
        messageSymbolTable.addValue("sender", event.getSender().getName());
        messageSymbolTable.addValue("home", event.getHome().getName());

        return new Scope(homeEditor.getScope(), messageSymbolTable);
    }

    protected static void say(final MessageSentEvent event, final String text) throws BotErrorException {
        Scope scope = getMessageScope(event);
        sayRaw(event, evaluate(event, scope, text));
    }

    protected static void say(final MessageSentEvent event, final SymbolTable commandSymbolTable, final String text)
            throws BotErrorException {
        Scope commandScope = new Scope(getMessageScope(event), commandSymbolTable);
        sayRaw(event, evaluate(event, commandScope, text));
    }

    protected static void sayRaw(final MessageSentEvent event, final String text) {
        Channel channel = event.getChannel();
        channel.say(text);
    }

    private static String evaluate(final Event event, final Scope scope, final String text) throws BotErrorException {
        StringBuilder result = new StringBuilder();
        Matcher matcher = REPLACEMENT_PATTERN.matcher(text);
        int currentIndex = 0;

        Parser parser = new Parser(scope, event.getHomeEditor());

        while (matcher.find()) {
            result.append(text.substring(currentIndex, matcher.start()));
            String expression = matcher.group(1);

            try {
                result.append(parser.parse(expression).evaluate());
            } catch (ParseException e) {
                throw new BotErrorException(String.format("Failed to parse %%%s%%: %s", expression, e.getMessage()));
            }

            currentIndex = matcher.end();
        }

        result.append(text.substring(currentIndex));
        return result.toString();
    }
}
