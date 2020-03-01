package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.scope.FunctorSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;

public class TextCommand extends MessageCommand {
    public static final int TYPE = 1;

    private final String text;

    public TextCommand(final int id, final int flags, final Permission permission, final String text)
            throws BotErrorException {
        super(id, flags, permission);
        this.text = text;

        Validation.validateStringLength(text, Validation.MAX_TEXT_LENGTH, "Command text");
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        FunctorSymbolTable symbolTable = new FunctorSymbolTable();
        symbolTable.addValue("input", arguments);
        MessageCommand.say(event, symbolTable, text);
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
