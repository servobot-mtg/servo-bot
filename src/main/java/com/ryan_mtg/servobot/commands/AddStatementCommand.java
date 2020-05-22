package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.utility.CommandParser;
import com.ryan_mtg.servobot.utility.Strings;

import java.util.Optional;
import java.util.regex.Pattern;

public class AddStatementCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.ADD_STATEMENT_COMMAND_TYPE;
    private static final Pattern BOOK_PATTERN = Pattern.compile("\\w+");
    private static final CommandParser COMMAND_PARSER = new CommandParser(BOOK_PATTERN);

    public AddStatementCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitAddStatementCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        CommandParser.ParseResult parseResult = COMMAND_PARSER.parse(arguments);

        String bookName = parseResult.getCommand();
        switch (parseResult.getStatus()) {
            case NO_COMMAND:
                throw new BotErrorException("No statement to add.");
            case COMMAND_MISMATCH:
                throw new BotErrorException(String.format("%s isn't properly formatted.", bookName));
        }

        String text = parseResult.getInput();
        if (Strings.isBlank(text)) {
            throw new BotErrorException("No statement to add.");
        }

        HomeEditor homeEditor = event.getHomeEditor();
        Optional<Book> book = homeEditor.getBook(bookName);
        if (book.isPresent()) {
            homeEditor.addStatement(book.get().getId(), text);
            MessageCommand.say(event, String.format("Statement added to %s.", book));
        } else if (Command.hasPermissions(event.getSender(), Permission.MOD)){
            homeEditor.addBook(bookName, text);
            MessageCommand.say(event, String.format("%s created with the statement.", bookName));
        } else {
            MessageCommand.say(event, String.format("No book with name %s.", bookName));
        }
    }
}
