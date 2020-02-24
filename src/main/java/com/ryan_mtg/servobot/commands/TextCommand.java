package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.scope.MessageSentSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;

public class TextCommand extends MessageCommand {
    public static final int TYPE = 1;
    public static final int MAX_TEXT_SIZE = CommandRow.MAX_STRING_SIZE;

    private final String text;

    public TextCommand(final int id, final int flags, final Permission permission, final String text)
            throws BotErrorException {
        super(id, flags, permission);
        this.text = text;
        if (text.length() > MAX_TEXT_SIZE) {
            throw new BotErrorException("Command text too long.");
        }
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        HomeEditor homeEditor = event.getHomeEditor();
        Scope scope = new Scope(homeEditor.getScope(), new MessageSentSymbolTable(event, arguments));
        MessageCommand.say(event, scope, text);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitTextCommand(this);
    }

    public String getText() {
        return text;
    }
}
