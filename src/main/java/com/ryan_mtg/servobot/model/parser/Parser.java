package com.ryan_mtg.servobot.model.parser;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.editors.StorageValueEditor;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.Evaluatable;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StringEvaluatable;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Time;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.ryan_mtg.servobot.model.parser.Token.Type.CLOSE_PARENTHESIS;
import static com.ryan_mtg.servobot.model.parser.Token.Type.CONDITIONAL;
import static com.ryan_mtg.servobot.model.parser.Token.Type.CONDITIONAL_ELSE;
import static com.ryan_mtg.servobot.model.parser.Token.Type.IDENTIFIER;
import static com.ryan_mtg.servobot.model.parser.Token.Type.INCREMENT;
import static com.ryan_mtg.servobot.model.parser.Token.Type.MEMBER_ACCESSOR;
import static com.ryan_mtg.servobot.model.parser.Token.Type.OPEN_PARENTHESIS;

public class Parser {
    private Lexer lexer;
    private Scope scope;
    private StorageValueEditor storageValueEditor;
    private Map<String, Object> constants = new HashMap<>();

    public Parser(final Scope scope, final StorageValueEditor storageValueEditor) {
        this.scope = scope;
        this.storageValueEditor = storageValueEditor;
        constants.put("true", true);
        constants.put("false", false);
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
        Object result = parseTermExpression();

        if (lexer.hasNextToken() && lexer.peekNextToken().getType() == CONDITIONAL) {
            expect(CONDITIONAL);
            Object trueOperand = parseExpression();
            expect(CONDITIONAL_ELSE);
            Object falseOperand = parseExpression();

            result = testCondition(result) ? trueOperand : falseOperand;
        }

        return result;
    }

    private Object parseTermExpression() throws ParseException {
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
            case STRING_LITERAL:
                String lexeme = lexer.getNextToken().getLexeme();
                result = lexeme.substring(1, lexeme.length() - 1);
                break;
            case IDENTIFIER:
                Token identifierToken = lexer.getNextToken();
                String identifier = identifierToken.getLexeme();
                result = constants.get(identifier);
                if (result == null) {
                    result = scope.lookup(identifier);
                }
                if (result == null) {
                    throw new ParseException(String.format("No value named %s.", identifier));
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
                    result = storageValueEditor.incrementStorageValue(((IntegerStorageValue) result).getName());
                } catch (UserError e) {
                    throw new ParseException(e.getMessage(), e);
                }
                break;
            case OPEN_PARENTHESIS:
                expect(OPEN_PARENTHESIS);
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
                storageValueEditor.incrementStorageValue(value.getName());
            } catch (UserError e) {
                throw new ParseException(e.getMessage(), e);
            }
        }

        return result;
    }

    private static boolean isTermOperation(final Token.Type tokenType) {
        return tokenType == Token.Type.ADD || tokenType == Token.Type.SUBTRACT;
    }

    private static boolean isFactorOperation(final Token.Type tokenType) {
        return tokenType == Token.Type.MULTIPLY;
    }

    private Object apply(final Token.Type operation, final Object leftHandOperand, final Object rightHandOperand)
            throws ParseException {
        switch (operation) {
            case ADD:
                return add(leftHandOperand, rightHandOperand);
            case SUBTRACT:
                return subtract(leftHandOperand, rightHandOperand);
            case MULTIPLY:
                return getInteger(leftHandOperand) * getInteger(rightHandOperand);
        }
        throw new ParseException(String.format("Invalid Operation '%s'", operation));
    }

    private Object add(final Object leftHandOperand, final Object rightHandOperand) throws ParseException {
        return getInteger(leftHandOperand) + getInteger(rightHandOperand);
    }

    private Object subtract(final Object leftHandOperand, final Object rightHandOperand) throws ParseException {
        if (leftHandOperand instanceof Temporal && rightHandOperand instanceof Temporal) {
            return Duration.between((Temporal) rightHandOperand, (Temporal) leftHandOperand);
        }
        return getInteger(leftHandOperand) - getInteger(rightHandOperand);
    }

    private boolean testCondition(final Object condition) throws ParseException {
        if (condition == null) {
            return false;
        }
        if (condition instanceof Boolean) {
            return (Boolean) condition;
        }
        if (condition instanceof String) {
            return !Strings.isBlank((String) condition);
        }
        throw new ParseException("Condition is not a boolean type.");
    }

    @SuppressWarnings("unchecked")
    private Object applyFunction(final Token functionToken, final Object function, final Object argument)
            throws ParseException {
        try {
            if (function instanceof Function) {
                return ((Function<Object, Object>)function).apply(argument);
            }
            throw new ParseException(String.format("Expected '%s' to be a function", functionToken.getLexeme()));
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

        if (value instanceof Boolean) {
            return new StringEvaluatable(Boolean.toString((boolean) value));
        }

        if (value instanceof Duration) {
            return new StringEvaluatable(Time.toReadableString((Duration) value));
        }

        if (value instanceof HomedUser) {
            return new StringEvaluatable(((HomedUser) value).getName());
        }

        if (value instanceof ZonedDateTime) {
            return new StringEvaluatable(Time.toReadableString((ZonedDateTime) value));
        }

        throw new ParseException(String.format("Unknown type '%s' for value: %s", value.getClass(), value));
    }
}
