package com.ryan_mtg.servobot.commands.chat;

import com.ryan_mtg.servobot.commands.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class TextCommand extends MessageCommand {
    public static final int TYPE = 1;

    @Getter
    private final String text;

    public TextCommand(final int id, final CommandSettings commandSettings, final String text)
            throws BotErrorException {
        super(id, commandSettings);
        this.text = text;

        Validation.validateStringLength(text, Validation.MAX_TEXT_LENGTH, "Command text");
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
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
}
