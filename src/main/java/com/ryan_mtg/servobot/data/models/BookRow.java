package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "book")
public class BookRow {
    public static final int MAX_NAME_SIZE = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    @Size(max = MAX_NAME_SIZE)
    private String name;

    public BookRow() {}

    public BookRow(final int id, final int botHomeId, final String name) {
        this.id = id;
        this.botHomeId = botHomeId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getBotHomeId() {
        return botHomeId;
    }

    public String getName() {
        return name;
    }
}
