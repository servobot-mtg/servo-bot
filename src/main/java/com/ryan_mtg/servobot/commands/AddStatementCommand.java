package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;

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

        String book = scanner.next();
        if (book.length() <= 1) {
            throw new BotErrorException("No statement to add.");
        }

        scanner.useDelimiter("\\z");

        if (!BOOK_PATTERN.matcher(book).matches()) {
            throw new BotErrorException(String.format("%s isn't properly formatted.", book));
        }

        if (!scanner.hasNext()) {
            throw new BotErrorException("No statement to add.");
        }

        String text = scanner.next().trim();

        if (text.isEmpty()) {
            throw new BotErrorException("No statement to add.");
        }

        HomeEditor homeEditor = event.getHomeEditor();
        homeEditor.addStatement(book, text);

        MessageCommand.say(event, String.format("Statement added to %s.", book));
    }
}
