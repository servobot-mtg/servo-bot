package com.ryan_mtg.servobot.commands.magic;

import lombok.Getter;

@Getter
public class CardQuery {
    private String query;
    private String set;

    public CardQuery(final String query) {
        this.query = query;
    }

    public CardQuery(final String query, final String set) {
        this.query = query;
        this.set = set;
    }
}
