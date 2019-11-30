package com.ryan_mtg.servobot;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.FactsCommand;
import com.ryan_mtg.servobot.data.factories.BookSerializer;
import com.ryan_mtg.servobot.data.factories.CommandSerializer;
import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class AdminTask implements Runnable {
    @Autowired
    private BookSerializer bookSerializer;

    @Autowired
    private CommandSerializer commandSerializer;

    @Autowired
    private Bot bot;

    @Override
    public void run() {
        createBooks();
        List<Book> books = bookSerializer.createBooks(1);
        BotHome botHome = bot.getHome(1);
        for(Command command : botHome.getCommandTable().getCommandList().values()) {
            if (command instanceof FactsCommand) {
                FactsCommand factsCommand = (FactsCommand) command;

                if (factsCommand.getBook().getId() == Book.UNREGISTERED_ID) {
                    for (Book book : books) {
                        if (book.getName().equals(factsCommand.getFileName())) {
                            factsCommand.setBook(book);
                            commandSerializer.saveCommand(1, factsCommand);
                        }
                    }

                }
            }
        }
    }

    private void createBooks() {
        createBook(1,  "CanadaFacts");
        createBook(1,  "CarolynFacts");
        createBook(1,  "CommandFacts");
        createBook(1,  "FrankFacts");
        createBook(1,  "MooseFacts");
        createBook(1,  "MooseLies");
        createBook(1,  "ServoFacts");
    }

    @Transactional
    public Book createBook(final int botHomeId, final String name) {
        List<Statement> lines = FactsCommand.readFacts(String.format("/facts/%s.txt", name));
        Book book = new Book(Book.UNREGISTERED_ID, name, lines);
        bookSerializer.saveBook(botHomeId, book);
        return book;
    }
}
