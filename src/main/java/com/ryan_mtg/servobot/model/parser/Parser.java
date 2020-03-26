package com.ryan_mtg.servobot.model.parser;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.Evaluatable;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StringEvaluatable;
import com.ryan_mtg.servobot.utility.Time;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.time.Duration;
import java.util.function.Function;

import static com.ryan_mtg.servobot.model.parser.Token.Type.CLOSE_PARENTHESIS;
import static com.ryan_mtg.servobot.model.parser.Token.Type.IDENTIFIER;
import static com.ryan_mtg.servobot.model.parser.Token.Type.INCREMENT;
import static com.ryan_mtg.servobot.model.parser.Token.Type.MEMBER_ACCESSOR;
import static com.ryan_mtg.servobot.model.parser.Token.Type.OPEN_PARENTHESIS;

public class Parser {
    private Lexer lexer;
    private Scope scope;
    private HomeEditor homeEditor;

    public Parser(final Scope scope, final HomeEditor homeEditor) {
        this.scope = scope;
        this.homeEditor = homeEditor;
    }

    public Evaluatable parse(final String expression) throws ParseException {
        lexer = new Lexer(expression);
        Object result = parseExpression();

        if (lexer.hasNextToken()) {
            throw new ParseException(
                    String.format("Illegal end of expression '%s'", lexer.peekNextToken().getLexeme()));
        }

        return convertToEvaluatable(result);
    }

    private Object parseExpression() throws ParseException {
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

                while (lexer.isNextToken(MEMBER_ACCESSOR)) {
                    lexer.getNextToken();
                    Token field = expect(IDENTIFIER);
                    result = accessMember(result, field.getLexeme());
                }

                if (lexer.isNextToken(OPEN_PARENTHESIS)) {
                    Object argument = parseFactor();
                    result = applyFunction(identifierToken, result, argument);
                }
                break;
            case INCREMENT:
                lexer.getNextToken();
                result = parseExpression();
                if (!(result instanceof IntegerStorageValue)) {
                    throw new ParseException("Invalid expression to increment");
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
                expect(CLOSE_PARENTHESIS);
                break;
            default:
                throw new ParseException(String.format("Illegal term '%s'", token.getLexeme()));
        }
        while (lexer.isNextToken(INCREMENT)) {
            lexer.getNextToken();
            if (!(result instanceof IntegerStorageValue)) {
                throw new ParseException("Invalid expression to increment");
            }
            IntegerStorageValue value = (IntegerStorageValue) result;
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

    private Object apply(final Token.Type operation, final Object leftHandOperand, final Object rightHandOperand)
            throws ParseException {
        switch (operation) {
            case ADD:
                return getInteger(leftHandOperand) + getInteger(rightHandOperand);
            case MULTIPLY:
                return getInteger(leftHandOperand) * getInteger(rightHandOperand);
        }
        throw new ParseException(String.format("Invalid Operation '%s'", operation));
    }

    @SuppressWarnings("unchecked")
    private Object applyFunction(final Token functionToken, final Object function, final Object argument)
            throws ParseException {
        if (!(function instanceof Function)) {
            throw new ParseException(String.format("Expected '%s' to be a function", functionToken.getLexeme()));
        }
        try {
            return ((Function<Object, Object>)function).apply(argument);
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Function class: " + function.getClass());
            System.out.println("Argument class: " + argument.getClass());
            throw new ParseException(String.format("Could not apply function '%s'", functionToken.getLexeme()));
        }
    }

    private Object accessMember(final Object object, final String field) throws ParseException {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                if (propertyDescriptor.getName().equals(field)) {
                    return propertyDescriptor.getReadMethod().invoke(object);
                }
            }
            throw new ParseException(String.format("No field %s", field));
        } catch (Exception e) {
            throw new ParseException(String.format("Unable to access field %s", field), e);
        }
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

    private Token expect(final Token.Type expectedType) throws ParseException {
        if (!lexer.hasNextToken()) {
            throw new ParseException(String.format("Expected '%s' instead of end of expression", expectedType));
        }
        if (lexer.peekNextToken().getType() != expectedType) {
            throw new ParseException(String.format("Expected '%s' instead of '%s'", expectedType,
                    lexer.peekNextToken().getLexeme()));
        }
        return lexer.getNextToken();
    }

    private Evaluatable convertToEvaluatable(final Object value) throws ParseException {
        if (value instanceof Evaluatable) {
            return (Evaluatable) value;
        }

        if (value instanceof String) {
            return new StringEvaluatable((String) value);
        }

        if (value instanceof Integer) {
            return new StringEvaluatable(Integer.toString((int) value));
        }

        if (value instanceof Duration) {
            return new StringEvaluatable(Time.toReadableString((Duration) value));
        }

        throw new ParseException(String.format("Unknown type '%s' for value: %s", value.getClass(), value));
    }
}
