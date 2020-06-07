package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.editors.BookTableEditor;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class AddBookedStatementCommand extends InvokedCommand {
    @Getter
    private Book book;

    @Getter
    private String response;

    public AddBookedStatementCommand(final int id, final CommandSettings commandSettings, final Book book,
            final String response) throws BotErrorException {
        super(id, commandSettings);
        this.book = book;
        this.response = response;

        Validation.validateStringLength(response, Validation.MAX_TEXT_LENGTH, "Response");
    }

    @Override
    public CommandType getType() {
        return CommandType.ADD_BOOKED_STATEMENT_COMMAND_TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitAddBookedStatementCommand(this);
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotErrorException {
        String text = event.getArguments();

        if (Strings.isBlank(text)) {
            throw new BotErrorException("No statement to add.");
        }

        BookTableEditor bookTableEditor = event.getBookTableEditor();
        bookTableEditor.addStatement(book.getId(), text);
        event.say(response);
    }
}
