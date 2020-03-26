package com.ryan_mtg.servobot.model.books;

import lombok.Getter;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class BookTableEdit {
    private List<Book> savedBooks = new ArrayList<>();
    private Map<Statement, Integer> savedStatements = new IdentityHashMap<>();
    private List<Statement> deletedStatements = new ArrayList<>();

    public void save(final Book book) {
        savedBooks.add(book);
    }

    public void save(final int bookId, final Statement statement) {
        savedStatements.put(statement, bookId);
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
