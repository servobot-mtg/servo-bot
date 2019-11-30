package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FactsCommand extends MessageCommand {
    public static final int TYPE = 2;

    private Book book;

    public FactsCommand(final int id, final boolean secure, final Permission permission, final String name,
                        final Book book) {
        super(id, secure, permission);
        if (book != null) {
            this.book = book;
        } else {
            this.book = readBook(name);
        }
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return book.getName() + " Command";
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitFactsCommand(this);
    }

    @Override
    public void perform(final Message message, final String arguments) {
        MessageCommand.say(message, book.getRandomLine());
    }

    public String getFileName() {
        return book.getName();
    }

    public Book getBook() {
        return book;
    }

    public void setBook(final Book book) {
        this.book = book;
    }

    private static Book readBook(final String name) {
        List<Statement> lines = readFacts(String.format("/facts/%s.txt", name));
        return new Book(Book.UNREGISTERED_ID, name, lines);
    }

    public static List<Statement> readFacts(final String resource) {
        Scanner scanner = new Scanner(FactsCommand.class.getResourceAsStream(resource));
        List<Statement> facts = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.length() > 0) {
                facts.add(new Statement(Statement.UNREGISTERED_ID, line));
            }
        }
        return facts;
    }
}
