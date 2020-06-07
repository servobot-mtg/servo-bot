package com.ryan_mtg.servobot.model.editors;

import com.ryan_mtg.servobot.data.factories.BookSerializer;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.books.BookTable;
import com.ryan_mtg.servobot.model.books.BookTableEdit;
import com.ryan_mtg.servobot.model.books.Statement;
import com.ryan_mtg.servobot.utility.Strings;

import javax.transaction.Transactional;
import java.util.Optional;

public class BookTableEditor {
    private final int contextId;
    private final BookTable bookTable;
    private final BookSerializer bookSerializer;

    public BookTableEditor(final int contextId, final BookTable bookTable, final BookSerializer bookSerializer) {
        this.contextId = contextId;
        this.bookTable = bookTable;
        this.bookSerializer = bookSerializer;
    }

    public Optional<Book> getBook(final String bookName) {
        return bookTable.getBook(bookName);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Book addBook(final String name, final String text) throws BotErrorException {
        Book book = new Book(Book.UNREGISTERED_ID, name);
        BookTableEdit bookTableEdit = bookTable.addBook(book);
        if (!Strings.isBlank(text)) {
            Statement statement = new Statement(Statement.UNREGISTERED_ID, text);
            bookTableEdit.merge(bookTable.addStatement(book, statement));
        }
        bookSerializer.commit(contextId, bookTableEdit);
        return book;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Statement addStatement(final int bookId, final String text) throws BotErrorException {
        Statement statement = new Statement(Statement.UNREGISTERED_ID, text);
        BookTableEdit bookTableEdit = bookTable.addStatement(bookId, statement);
        bookSerializer.commit(contextId, bookTableEdit);
        return statement;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void deleteStatement(final int bookId, final int statementId) throws BotErrorException {
        BookTableEdit bookTableEdit = bookTable.deleteStatement(bookId, statementId);
        bookSerializer.commit(contextId, bookTableEdit);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void modifyStatement(final int bookId, final int statementId, final String text) throws BotErrorException {
        BookTableEdit bookTableEdit = new BookTableEdit();
        Book book = bookTable.getBook(bookId);
        Statement statement = book.getStatement(statementId);
        statement.setText(text);
        bookTableEdit.save(bookId, statement);
        bookSerializer.commit(contextId, bookTableEdit);
    }
}
