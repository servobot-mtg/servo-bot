package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "book")
@Getter
public class BookRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    @Size(max = Validation.MAX_NAME_LENGTH)
    private String name;

    public BookRow() {}

    public BookRow(final int id, final int botHomeId, final String name) {
        this.id = id;
        this.botHomeId = botHomeId;
        this.name = name;
    }
}
