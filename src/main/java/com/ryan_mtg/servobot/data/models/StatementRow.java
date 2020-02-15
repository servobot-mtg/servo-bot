package com.ryan_mtg.servobot.data.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "statement")
public class StatementRow {
    public static final int MAX_TEXT_SIZE = 256;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int bookId;

    @Size(max = MAX_TEXT_SIZE)
    private String text;

    public StatementRow() {}

    public StatementRow(final int id, final int bookId, final String text) {
        this.id = id;
        this.bookId = bookId;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }

    public String getText() {
        return text;
    }
}
