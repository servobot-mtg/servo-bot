package com.ryan_mtg.servobot.model.parser;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class TokenMatcher extends TypeSafeMatcher<Token> {
    private Token.Type type;
    private String lexeme;

    public TokenMatcher(final Token.Type type, final String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    @Override
    protected boolean matchesSafely(final Token token) {
        return token.getType() == type && token.getLexeme().equals(lexeme);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("is a Token with type ").appendValue(type)
                .appendText("and lexeme ").appendValue(lexeme);
    }
}
