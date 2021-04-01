package com.ryan_mtg.servobot.commands.magic;

import lombok.Getter;

@Getter
public class CardQuery {
    private final String query;
    private final String set;

    public CardQuery(final String query) {
        this(query, null);
    }

    public CardQuery(final String query, final String set) {
        this.query = query;
        this.set = set;
    }
}
