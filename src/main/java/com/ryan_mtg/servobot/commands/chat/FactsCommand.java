package com.ryan_mtg.servobot.commands.chat;

import com.ryan_mtg.servobot.commands.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.books.Book;
import lombok.Getter;
import lombok.Setter;

public class FactsCommand extends MessageCommand {
    public static final int TYPE = 2;

    @Getter @Setter
    private Book book;

    public FactsCommand(final int id, final CommandSettings commandSettings, final Book book) {
        super(id, commandSettings);
        this.book = book;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitFactsCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        MessageCommand.say(event, book.getRandomLine());
    }
}
