package com.ryan_mtg.servobot.data.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "bot")
public class BotRow {
    public static final int MAX_NAME_SIZE = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = MAX_NAME_SIZE)
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
