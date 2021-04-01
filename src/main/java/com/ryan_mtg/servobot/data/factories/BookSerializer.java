package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.BookRow;
import com.ryan_mtg.servobot.data.models.StatementRow;
import com.ryan_mtg.servobot.data.repositories.BookRepository;
import com.ryan_mtg.servobot.data.repositories.StatementRepository;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.books.BookTable;
import com.ryan_mtg.servobot.model.books.BookTableEdit;
import com.ryan_mtg.servobot.model.books.Statement;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BookSerializer {
    private final BookRepository bookRepository;
    private final StatementRepository statementRepository;

    public BookSerializer(final BookRepository bookRepository, final StatementRepository statementRepository) {
        this.bookRepository = bookRepository;
        this.statementRepository = statementRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public void saveBook(final int botHomeId, final Book book) {
        BookRow bookRow = new BookRow(book.getId(), botHomeId, book.getName());
        bookRepository.save(bookRow);
        int bookId = bookRow.getId();
        book.setId(bookId);

        for (Statement statement : book.getStatements()) {
            saveStatement(bookId, statement);
        }
    }

    public BookTable createBookTable(int botHomeId) {
        List<Book> books = new ArrayList<>();

        Iterable<BookRow> bookRows = bookRepository.findAllByBotHomeId(botHomeId);
        Iterable<Integer> bookIds = SerializationSupport.getIds(bookRows, BookRow::getId);

        Map<Integer, List<StatementRow>> statementRowMap = SerializationSupport.getIdMapping(
            statementRepository.findAllByBookIdIn(bookIds), bookIds, StatementRow::getBookId);

        return SystemError.filter(() -> {
            for(BookRow bookRow : bookRepository.findAllByBotHomeId(botHomeId)) {
                List<Statement> statements = new ArrayList<>();
                for (StatementRow statementRow : statementRowMap.get(bookRow.getId())) {
                    statements.add(new Statement(statementRow.getId(), statementRow.getText()));
                }
                books.add(new Book(bookRow.getId(), bookRow.getName(), statements));
            }
            return new BookTable(books);
        });
    }

    public void saveStatement(final int bookId, final Statement statement) {
        StatementRow statementRow = new StatementRow(statement.getId(), bookId, statement.getText());
        statementRepository.save(statementRow);
        statement.setId(statementRow.getId());
    }

    public void commit(final int botHomeId, final BookTableEdit bookTableEdit) {
        for (Book book : bookTableEdit.getSavedBooks()) {
            saveBook(botHomeId, book);
            bookTableEdit.bookSaved(book);
        }

        for (Statement statement : bookTableEdit.getDeletedStatements()) {
            statementRepository.deleteById(statement.getId());
        }

        for (Map.Entry<Statement, Integer>  entry : bookTableEdit.getSavedStatements().entrySet()) {
            saveStatement(entry.getValue(), entry.getKey());
        }
    }
}
