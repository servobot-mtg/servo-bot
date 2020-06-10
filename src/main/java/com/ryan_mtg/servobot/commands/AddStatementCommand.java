package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.LibraryError;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.editors.BookTableEditor;
import com.ryan_mtg.servobot.utility.CommandParser;
import com.ryan_mtg.servobot.utility.Strings;

import java.util.Optional;
import java.util.regex.Pattern;

public class AddStatementCommand extends InvokedCommand {
    private static final Pattern BOOK_PATTERN = Pattern.compile("\\w+");
    private static final CommandParser COMMAND_PARSER = new CommandParser(BOOK_PATTERN);

    public AddStatementCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public CommandType getType() {
        return CommandType.ADD_STATEMENT_COMMAND_TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitAddStatementCommand(this);
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotHomeError, UserError {
        CommandParser.ParseResult parseResult = COMMAND_PARSER.parse(event.getArguments());

        String bookName = parseResult.getCommand();
        switch (parseResult.getStatus()) {
            case NO_COMMAND:
                throw new UserError("No statement to add.");
            case COMMAND_MISMATCH:
                throw new UserError("%s isn't properly formatted.", bookName);
        }

        String text = parseResult.getInput();
        if (Strings.isBlank(text)) {
            throw new UserError("No statement to add.");
        }

        BookTableEditor bookTableEditor = event.getBookTableEditor();
        Optional<Book> book = bookTableEditor.getBook(bookName);
        if (book.isPresent()) {
            try {
                bookTableEditor.addStatement(book.get().getId(), text);
            } catch (LibraryError e) {
                throw new SystemError(e.getMessage(), e);
            }
            event.say(String.format("Statement added to %s.", book));
        } else if (Command.hasPermissions(event.getSender(), Permission.MOD)){
            bookTableEditor.addBook(bookName, text);
            event.say(String.format("%s created with the statement.", bookName));
        } else {
            event.say(String.format("No book with name %s.", bookName));
        }
    }
}
