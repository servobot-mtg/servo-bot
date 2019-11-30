package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.Message;

public class FactsCommand extends MessageCommand {
    public static final int TYPE = 2;

    private Book book;

    public FactsCommand(final int id, final boolean secure, final Permission permission, final Book book) {
        super(id, secure, permission);
        this.book = book;
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
}
