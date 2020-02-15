package com.ryan_mtg.servobot.data.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "suggestion")
public class SuggestionRow {
    public static final int MAX_SUGGESTION_SIZE = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = MAX_SUGGESTION_SIZE)
    private String alias;

    private int count;

    public SuggestionRow() {}

    public SuggestionRow(final String alias, final int count) {
        this.alias = alias;
        this.count = count;
    }

    public String getAlias() {
        return alias;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }
}
