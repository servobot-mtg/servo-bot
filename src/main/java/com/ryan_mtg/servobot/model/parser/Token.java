package com.ryan_mtg.servobot.model.parser;

import lombok.Getter;

public class Token {
    public enum Type {
        IDENTIFIER,
        INTEGER,
        OPEN_PARENTHESIS,
        CLOSE_PARENTHESIS,
        MEMBER_ACCESSOR,
        INCREMENT,
        ADD,
        MULTIPLY,
        CONDITIONAL,
        CONDITIONAL_ELSE,
        STRING_LITERAL,
        SUBTRACT,
        DECREMENT
    }

    @Getter
    private final Type type;

    @Getter
    private final String lexeme;

    public Token(final Type type, final String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }
}
