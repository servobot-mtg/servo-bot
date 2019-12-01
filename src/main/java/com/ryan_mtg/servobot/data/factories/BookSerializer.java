package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.BookRow;
import com.ryan_mtg.servobot.data.models.StatementRow;
import com.ryan_mtg.servobot.data.repositories.BookRepository;
import com.ryan_mtg.servobot.data.repositories.StatementRepository;
import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookSerializer {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StatementRepository statementRepository;

    @Transactional
    public void saveBook(final int botHomeId, final Book book) {
        BookRow bookRow = new BookRow(book.getId(), botHomeId, book.getName());
        bookRepository.save(bookRow);
        int bookId = bookRow.getId();
        book.setId(bookId);

        for (Statement statement : book.getStatements()) {
            saveStatement(bookId, statement);
        }
    }

    public List<Book> createBooks(final int botHomeId) {
        List<Book> books = new ArrayList<>();
        for(BookRow bookRow : bookRepository.findAllByBotHomeId(botHomeId)) {
            List<Statement> statements = statementRepository.findAllByBookId(bookRow.getId()).stream()
                    .map(statementRow -> new Statement(statementRow.getId(), statementRow.getText()))
                    .collect(Collectors.toList());
            books.add(new Book(bookRow.getId(), bookRow.getName(), statements));
        }
        return books;
    }

    public void saveStatement(final int bookId, final Statement statement) {
        StatementRow statementRow = new StatementRow(statement.getId(), bookId, statement.getText());
        statementRepository.save(statementRow);
        statement.setId(statementRow.getId());
    }
}
