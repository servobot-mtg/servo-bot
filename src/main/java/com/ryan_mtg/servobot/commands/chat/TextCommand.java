package com.ryan_mtg.servobot.commands.chat;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class TextCommand extends InvokedCommand {
    public static final CommandType TYPE = CommandType.TEXT_COMMAND_TYPE;

    @Getter
    private String text;

    public TextCommand(final int id, final CommandSettings commandSettings, final String text) throws UserError {
        super(id, commandSettings);
        setText(text);
    }

    public void setText(final String text) throws UserError {
        Validation.validateStringLength(text, Validation.MAX_TEXT_LENGTH, "Command text");
        this.text = text;
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotHomeError {
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
