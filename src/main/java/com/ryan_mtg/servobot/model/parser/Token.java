package com.ryan_mtg.servobot.model.parser;

public class Token {
    public static enum Type {
        IDENTIFIER,
        INTEGER,
        OPEN_PARENTHESIS,
        CLOSE_PARENTHESIS,
        INCREMENT,
        ADD,
        MULTIPLY,
    }

    private Type type;
    private String lexeme;

    public Token(final Type type, final String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public Type getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }
}
