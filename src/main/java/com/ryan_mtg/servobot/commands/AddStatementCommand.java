package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.books.Book;

import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AddStatementCommand extends MessageCommand {
    public static final int TYPE = 15;
    private static final Pattern BOOK_PATTERN = Pattern.compile("\\w+");

    public AddStatementCommand(final int id, final int flags, final Permission permission) {
        super(id, flags, permission);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitAddStatementCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        if (arguments == null) {
            throw new BotErrorException("No statement to add.");
        }

        Scanner scanner = new Scanner(arguments);

        String bookName = scanner.next();
        if (bookName.length() <= 1) {
            throw new BotErrorException("No statement to add.");
        }

        scanner.useDelimiter("\\z");

        if (!BOOK_PATTERN.matcher(bookName).matches()) {
            throw new BotErrorException(String.format("%s isn't properly formatted.", bookName));
        }

        if (!scanner.hasNext()) {
            throw new BotErrorException("No statement to add.");
        }

        String text = scanner.next().trim();

        if (text.isEmpty()) {
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
