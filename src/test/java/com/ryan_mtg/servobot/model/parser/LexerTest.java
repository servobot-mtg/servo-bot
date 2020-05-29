package com.ryan_mtg.servobot.model.parser;

import org.junit.Test;

import static com.ryan_mtg.servobot.model.parser.Matchers.isAToken;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LexerTest {
    @Test
    public void testOpenParenthesis() {
        Lexer lexer = new Lexer("(");
        assertTrue(lexer.hasNextToken());

        assertThat(lexer.getNextToken(), isAToken(Token.Type.OPEN_PARENTHESIS, "("));
        assertFalse(lexer.hasNextToken());
    }

    @Test
    public void testCloseParenthesis() {
        Lexer lexer = new Lexer(")");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.CLOSE_PARENTHESIS, ")"));
    }


    @Test
    public void testIncrement() {
        Lexer lexer = new Lexer("++");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.INCREMENT, "++"));
    }

    @Test
    public void testIdentifier() {
        Lexer lexer = new Lexer("hello123");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.IDENTIFIER, "hello123"));
    }

    @Test
    public void testInteger() {
        Lexer lexer = new Lexer("123");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.INTEGER, "123"));
    }

    @Test
    public void testAdd() {
        Lexer lexer = new Lexer("+");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.ADD, "+"));
    }

    @Test
    public void testMultiply() {
        Lexer lexer = new Lexer("*");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.MULTIPLY, "*"));
    }

    @Test
    public void testConditional() {
        Lexer lexer = new Lexer("?");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.CONDITIONAL, "?"));
    }

    @Test
    public void testConditionalElse() {
        Lexer lexer = new Lexer(":");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.CONDITIONAL_ELSE, ":"));
    }

    @Test
    public void testIncrementThenIdentifier() {
        Lexer lexer = new Lexer("++hello");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.INCREMENT, "++"));
        assertThat(lexer.getNextToken(), isAToken(Token.Type.IDENTIFIER, "hello"));
        assertFalse(lexer.hasNextToken());
    }
}