package com.ryan_mtg.servobot.model.parser;

public class Matchers {
    public static TokenMatcher isAToken(final Token.Type type, final String lexme) {
        return new TokenMatcher(type, lexme);
    }
}
