package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "storage_value")
public class StorageValueRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    private int type;

    @Column(name = "user_id")
    private int userId;

    @Size(max = Validation.MAX_NAME_LENGTH)
    private String name;

    private int number;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String string;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setBotHomeId(final int botHomeId) {
        this.botHomeId = botHomeId;
    }

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public String getString() {
        return string;
    }

    public void setString(final String string) {
        this.string = string;
    }
}
