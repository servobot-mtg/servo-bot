package com.ryan_mtg.servobot.model.books;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.storage.Evaluatable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public Book(final int id, final String name) throws BotErrorException {
        this(id, name, new ArrayList<>());
    }

    public Book(final int id, final String name, final List<Statement> statements) throws BotErrorException {
        this.id = id;
        this.name = name;
        this.statements = statements;

        Validation.validateStringLength(name, Validation.MAX_NAME_LENGTH, "Name");
    }

    public Statement getStatement(final int statementId) throws BotErrorException {
        Optional<Statement> statement = statements.stream().filter(s -> s.getId() == statementId).findFirst();
        if (statement.isPresent()) {
            return statement.get();
        }
        throw new BotErrorException(String.format("No statement with id %d", statementId));
    }

    public String getRandomLine() {
        if (statements.isEmpty()) {
            return "";
        }
        return statements.get(RANDOM.nextInt(statements.size())).getText();
    }

    public void addStatement(final Statement statement) {
        statements.add(statement);
    }

    public Statement deleteStatement(final int statementId) throws BotErrorException {
        Statement statement = getStatement(statementId);
        statements.remove(statement);
        return statement;
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
