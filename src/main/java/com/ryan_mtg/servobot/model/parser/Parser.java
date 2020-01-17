package com.ryan_mtg.servobot.model.parser;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StorageValue;

public class Parser {
    private Lexer lexer;
    private Scope scope;
    private HomeEditor homeEditor;

    public Parser(final Scope scope, final HomeEditor homeEditor) {
        this.scope = scope;
        this.homeEditor = homeEditor;
    }

    public Object parse(final String expression) throws ParseException {
        lexer = new Lexer(expression);
        Object result = parseExpression();

        if (lexer.hasNextToken()) {
            throw new ParseException(
                    String.format("Illegal end of expression '%s'", lexer.peekNextToken().getLexeme()));
        }

        if (result instanceof StorageValue) {
            return ((StorageValue) result).getValue();
        }
        return result;
    }

    public Object parseExpression() throws ParseException {
        Object result = parseTerm();

        while (lexer.hasNextToken() && isTermOperation(lexer.peekNextToken().getType())) {
            Token operationToken = lexer.getNextToken();
            Object rightHandOperand = parseTerm();
            result = apply(operationToken.getType(), result, rightHandOperand);
        }
        return result;
    }

    private Object parseTerm() throws ParseException {
        if (!lexer.hasNextToken()) {
            throw new ParseException("Illegal end of expression");
        }

        Object result = parseFactor();

        while (lexer.hasNextToken() && isFactorOperation(lexer.peekNextToken().getType())) {
            Token operationToken = lexer.getNextToken();
            Object rightHandOperand = parseFactor();
            result = apply(operationToken.getType(), result, rightHandOperand);
        }
        return result;
    }

    private Object parseFactor() throws ParseException {
        Token token = lexer.peekNextToken();
        Object result;
        switch (token.getType()) {
            case INTEGER:
                result = Integer.parseInt(lexer.getNextToken().getLexeme());
                break;
            case IDENTIFIER:
                Token identifierToken = lexer.getNextToken();
                result = scope.lookup(identifierToken.getLexeme());
                if (result == null) {
                    throw new ParseException(String.format("No value named %s.", identifierToken.getLexeme()));
                }
                break;
            case INCREMENT:
                lexer.getNextToken();
                result = parseExpression();
                if (!(result instanceof IntegerStorageValue)) {
                    throw new ParseException(
                            String.format("Invalid expression to increment"));
                }
                try {
                    result = homeEditor.incrementStorageValue(((IntegerStorageValue) result).getName());
                } catch (BotErrorException e) {
                    throw new ParseException(e.getErrorMessage());
                }
                break;
            case OPEN_PARENTHESIS:
                lexer.getNextToken();
                result = parseExpression();
                if (!lexer.hasNextToken() || lexer.peekNextToken().getType() != Token.Type.CLOSE_PARENTHESIS) {
                    throw new ParseException(
                            String.format("Expected ')' instead of '%s'", lexer.peekNextToken().getLexeme()));
                }
                lexer.getNextToken();
                break;
            default:
                throw new ParseException(String.format("Illegal term '%s'", token.getLexeme()));
        }
        while (lexer.hasNextToken() && lexer.peekNextToken().getType() == Token.Type.INCREMENT) {
            lexer.getNextToken();
            if (!(result instanceof IntegerStorageValue)) {
                throw new ParseException(
                        String.format("Invalid expression to increment"));
            }
            IntegerStorageValue value = (IntegerStorageValue)  result;
            result = value.getValue();
            try {
                homeEditor.incrementStorageValue(value.getName());
            } catch (BotErrorException e) {
                throw new ParseException(e.getErrorMessage());
            }
        }

        return result;
    }

    private static boolean isTermOperation(final Token.Type tokenType) {
        return tokenType == Token.Type.ADD;
    }

    private static boolean isFactorOperation(final Token.Type tokenType) {
        return tokenType == Token.Type.MULTIPLY;
    }

    private Object apply(final Token.Type operation, final Object leftHandOpeerand, final Object rightHandOperand)
            throws ParseException {
        switch (operation) {
            case ADD:
                return getInteger(leftHandOpeerand) + getInteger(rightHandOperand);
            case MULTIPLY:
                return getInteger(leftHandOpeerand) * getInteger(rightHandOperand);
        }
        throw new ParseException(String.format("Invalid Operation '%s'", operation));
    }

    private int getInteger(final Object object) throws ParseException {
        if (object instanceof Integer) {
            return (Integer) object;
        }

        if (object instanceof IntegerStorageValue) {
            return ((IntegerStorageValue) object).getValue();
        }

        throw new ParseException(String.format("%s is not an integer", object));
    }
}
