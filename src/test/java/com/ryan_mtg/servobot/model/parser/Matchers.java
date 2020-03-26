package com.ryan_mtg.servobot.model.parser;

public class Matchers {
    public static TokenMatcher isAToken(final Token.Type type, final String lexeme) {
        return new TokenMatcher(type, lexeme);
    }
}
