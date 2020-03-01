package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "bot")
public class BotRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = Validation.MAX_NAME_LENGTH)
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
