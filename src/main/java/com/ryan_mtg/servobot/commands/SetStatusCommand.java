package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.Book;

public class SetStatusCommand extends HomeCommand {
    public static final int TYPE = 14;
    private Book book;

    public SetStatusCommand(final int id, final int flags, final Permission permission, final Book book) {
        super(id, flags, permission);
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

    @Override
    public void perform(final HomeEvent homeEvent) {
        homeEvent.getHome().setStatus(book.getRandomLine());
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
