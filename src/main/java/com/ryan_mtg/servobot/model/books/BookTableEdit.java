package com.ryan_mtg.servobot.model.books;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class BookTableEdit {
    private List<Book> savedBooks = new ArrayList<>();
    private Map<Statement, Integer> savedStatements = new IdentityHashMap<>();
    private List<Statement> deletedStatements = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    private Map<Book, Statement> savedBookToStatementMap = new HashMap<>();

    public void save(final Book book) {
        savedBooks.add(book);
    }

    public void save(final Book book, final Statement statement) {
        if (book.getId() == Book.UNREGISTERED_ID) {
            savedBookToStatementMap.put(book, statement);
        } else {
            save(book.getId(), statement);
        }
    }

    public void save(final int bookId, final Statement statement) {
        savedStatements.put(statement, bookId);
    }

    public void bookSaved(final Book book) {
        if (savedBookToStatementMap.containsKey(book)) {
            savedStatements.put(savedBookToStatementMap.get(book), book.getId());
        }
    }

    public void delete(final Statement statement) {
        deletedStatements.add(statement);
    }

    public void merge(final BookTableEdit bookTableEdit) {
        savedBooks.addAll(bookTableEdit.getSavedBooks());
        savedStatements.putAll(bookTableEdit.getSavedStatements());
        deletedStatements.addAll(bookTableEdit.getDeletedStatements());
    }
}
