package com.ryan_mtg.servobot.model;

import java.util.List;
import java.util.Random;

public class Book {
    public static final int UNREGISTERED_ID = 0;
    private static final Random RANDOM = new Random();

    private int id;
    private String name;
    private List<Statement> statements;

    public Book(final int id, final String name, final List<Statement> statements) {
        this.id = id;
        this.name = name;
        this.statements = statements;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public String getRandomLine() {
        return statements.get(RANDOM.nextInt(statements.size())).getText();
    }

    public void deleteStatement(final int statementId) {
        statements.removeIf(statement -> statement.getId() == statementId);
    }
}
