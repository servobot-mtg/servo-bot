package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.books.Book;
import lombok.Getter;

public class SetStatusCommand extends HomeCommand {
    public static final CommandType TYPE = CommandType.SET_STATUS_COMMAND_TYPE;

    @Getter
    private Book book;

    public SetStatusCommand(final int id, final CommandSettings commandSettings, final Book book) {
        super(id, commandSettings);
        this.book = book;
    }

    @Override
    public void perform(final HomeEvent homeEvent) {
        homeEvent.getServiceHome().setStatus(book.getRandomLine());
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSetStatusCommand(this);
    }
}
