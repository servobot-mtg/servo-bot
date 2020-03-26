package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.books.Book;
import lombok.Getter;

public class SetStatusCommand extends HomeCommand {
    public static final int TYPE = 14;

    @Getter
    private Book book;

    public SetStatusCommand(final int id, final CommandSettings commandSettings, final Book book) {
        super(id, commandSettings);
        this.book = book;
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
