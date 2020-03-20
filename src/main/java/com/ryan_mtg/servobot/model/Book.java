package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.storage.Evaluatable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Book implements Evaluatable, Function<Integer, String> {
    public static final int UNREGISTERED_ID = 0;

    private static final Random RANDOM = new Random();

    @Getter @Setter
    private int id;

    @Getter
    private String name;

    @Getter
    private List<Statement> statements;

    public Book(final int id, final String name, final List<Statement> statements) throws BotErrorException {
        this.id = id;
        this.name = name;
        this.statements = statements;

        Validation.validateStringLength(name, Validation.MAX_NAME_LENGTH, "Name");
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

    @Override
    public String evaluate() {
        return getRandomLine();
    }

    @Override
    public String apply(final Integer index) {
        return statements.get(index % statements.size()).getText();
    }

    public static String randomStatement(final Book book) {
        return book.getRandomLine();
    }
}
