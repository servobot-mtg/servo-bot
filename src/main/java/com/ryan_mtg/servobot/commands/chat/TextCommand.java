package com.ryan_mtg.servobot.commands.chat;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class TextCommand extends InvokedCommand {
    public static final CommandType TYPE = CommandType.TEXT_COMMAND_TYPE;

    @Getter
    private final String text;

    public TextCommand(final int id, final CommandSettings commandSettings, final String text)
            throws BotErrorException {
        super(id, commandSettings);
        this.text = text;

        Validation.validateStringLength(text, Validation.MAX_TEXT_LENGTH, "Command text");
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotErrorException {
        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        String input = event.getArguments() != null ? event.getArguments() : "";
        symbolTable.addValue("input", input);
        event.say(symbolTable, text);
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitTextCommand(this);
    }
}
