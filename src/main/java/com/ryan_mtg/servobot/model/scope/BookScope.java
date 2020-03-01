package com.ryan_mtg.servobot.model.scope;

import com.ryan_mtg.servobot.model.Book;

import java.util.List;

public class BookScope implements SymbolTable {
    private final List<Book> books;

    public BookScope(final List<Book> books) {
        this.books = books;
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
}
