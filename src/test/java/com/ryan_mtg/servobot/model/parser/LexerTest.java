package com.ryan_mtg.servobot.model.parser;

import org.junit.Test;

import static com.ryan_mtg.servobot.model.parser.Matchers.isAToken;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LexerTest {
    @Test
    public void testOpenParenthesis() throws ParseException {
        Lexer lexer = new Lexer("(");
        assertTrue(lexer.hasNextToken());

        assertThat(lexer.getNextToken(), isAToken(Token.Type.OPEN_PARENTHESIS, "("));
        assertFalse(lexer.hasNextToken());
    }

    @Test
    public void testCloseParenthesis() throws ParseException {
        Lexer lexer = new Lexer(")");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.CLOSE_PARENTHESIS, ")"));
    }


    @Test
    public void testIncrement() throws ParseException {
        Lexer lexer = new Lexer("++");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.INCREMENT, "++"));
    }

    @Test
    public void testIdentifier() throws ParseException {
        Lexer lexer = new Lexer("hello123");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.IDENTIFIER, "hello123"));
    }

    @Test
    public void testInteger() throws ParseException {
        Lexer lexer = new Lexer("123");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.INTEGER, "123"));
    }

    @Test
    public void testStringLiteral() throws ParseException {
        Lexer lexer = new Lexer("\"Hello\"");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.STRING_LITERAL, "\"Hello\""));
    }

    @Test
    public void testStringLiteralWithSingleQuotes() throws ParseException {
        Lexer lexer = new Lexer("'hi'");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.STRING_LITERAL, "'hi'"));
    }

    @Test
    public void testAdd() throws ParseException {
        Lexer lexer = new Lexer("+");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.ADD, "+"));
    }

    @Test
    public void testMultiply() throws ParseException {
        Lexer lexer = new Lexer("*");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.MULTIPLY, "*"));
    }

    @Test
    public void testConditional() throws ParseException {
        Lexer lexer = new Lexer("?");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.CONDITIONAL, "?"));
    }

    @Test
    public void testConditionalElse() throws ParseException {
        Lexer lexer = new Lexer(":");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.CONDITIONAL_ELSE, ":"));
    }

    @Test
    public void testIncrementThenIdentifier() throws ParseException {
        Lexer lexer = new Lexer("++hello");
        assertThat(lexer.getNextToken(), isAToken(Token.Type.INCREMENT, "++"));
        assertThat(lexer.getNextToken(), isAToken(Token.Type.IDENTIFIER, "hello"));
        assertFalse(lexer.hasNextToken());
    }
}