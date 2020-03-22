package com.ryan_mtg.servobot.model.books;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.scope.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BookTable implements SymbolTable, Iterable<Book>{
    private final List<Book> books;

    public BookTable() {
        this(new ArrayList<>());
    }

    public BookTable(final List<Book> books) {
        this.books = books;
    }

    @Override
    public Iterator<Book> iterator() {
        return books.iterator();
    }

    @Override
    public Book lookup(final String name) {
        for (Book book : books) {
            if (name.equals(book.getName())) {
                return book;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return books.isEmpty();
    }

    public Book getBook(final int bookId) throws BotErrorException {
        Optional<Book> book = books.stream().filter(b -> b.getId() == bookId).findFirst();
        if (book.isPresent()) {
            return book.get();
        }
        throw new BotErrorException(String.format("No book with id %d", bookId));
    }

    public Optional<Book> getBook(final String bookName) {
        return books.stream().filter(b -> b.getName().equalsIgnoreCase(bookName)).findFirst();
    }

    public BookTableEdit addBook(final Book book) {
        BookTableEdit bookTableEdit = new BookTableEdit();
        books.add(book);
        bookTableEdit.save(book);
        return bookTableEdit;
    }

    public BookTableEdit addStatement(final int bookId, final Statement statement) throws BotErrorException {
        return addStatement(getBook(bookId), statement);
    }

    public BookTableEdit addStatement(final Book book, final Statement statement) {
        BookTableEdit bookTableEdit = new BookTableEdit();
        book.addStatement(statement);
        bookTableEdit.save(book.getId(), statement);
        return bookTableEdit;
    }

    public BookTableEdit deleteStatement(final int bookId, final int statementId) throws BotErrorException {
        BookTableEdit bookTableEdit = new BookTableEdit();
        Book book = getBook(bookId);
        Statement statement = book.deleteStatement(statementId);
        bookTableEdit.delete(statement);
        return bookTableEdit;
    }

    public Map<Integer, Book> getBookMap() {
        Map<Integer, Book> bookMap = new HashMap<>();
        for (Book book : books) {
            bookMap.put(book.getId(), book);
        }
        return bookMap;
    }
}
