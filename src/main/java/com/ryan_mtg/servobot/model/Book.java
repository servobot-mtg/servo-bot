package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.data.models.BookRow;
import com.ryan_mtg.servobot.events.BotErrorException;

import java.util.List;
import java.util.Random;

public class Book {
    public static final int UNREGISTERED_ID = 0;

    private static final Random RANDOM = new Random();
    private static final int MAX_NAME_SIZE = BookRow.MAX_NAME_SIZE;

    private int id;
    private String name;
    private List<Statement> statements;

    public Book(final int id, final String name, final List<Statement> statements) throws BotErrorException {
        this.id = id;
        this.name = name;
        this.statements = statements;

        if (name.length() > MAX_NAME_SIZE) {
            throw new BotErrorException(String.format("Name too long (max %d): %s", MAX_NAME_SIZE, name));
        }
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

    public void addStatement(final Statement statement) {
        statements.add(statement);
    }

    public void deleteStatement(final int statementId) {
        statements.removeIf(statement -> statement.getId() == statementId);
    }
}
