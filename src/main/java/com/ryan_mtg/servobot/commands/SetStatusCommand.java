package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.Home;

public class SetStatusCommand extends HomeCommand {
    public static final int TYPE = 14;
    private Book book;

    public SetStatusCommand(final int id, final CommandSettings commandSettings, final Book book) {
        super(id, commandSettings);
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

    @Override
    public void perform(final Home home) {
        home.setStatus(book.getRandomLine());
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSetStatusCommand(this);
    }
}
