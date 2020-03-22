package com.ryan_mtg.servobot.model.parser;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.books.Statement;
import com.ryan_mtg.servobot.model.scope.FunctorSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import lombok.Getter;
import lombok.Setter;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.ryan_mtg.servobot.model.ObjectMother.mockHomeEditor;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;

public class ParserTest {
    private static final String TEXT = "string text";

    private Parser parser;
    private Scope scope;
    private FunctorSymbolTable functorSymbolTable;
    private HomeEditor homeEditor;

    @Before
    public void setUp() {
        functorSymbolTable = new FunctorSymbolTable();
        scope = new Scope(null, functorSymbolTable);
        homeEditor = mockHomeEditor();
        parser = new Parser(scope, homeEditor);
    }

    @Test
    public void testIntegerExpression() throws ParseException {
        assertEquals("1", parser.parse("1").evaluate());
    }

    @Test
    public void testParenthesisExpression() throws ParseException {
        assertEquals("2", parser.parse("(2)").evaluate());
    }

    @Test
    public void testAdditionExpression() throws ParseException {
        assertEquals("3", parser.parse("1+2").evaluate());
    }

    @Test
    public void testMultiplicationExpression() throws ParseException {
        assertEquals("6", parser.parse("2*3").evaluate());
    }

    @Test
    public void testOrderOfOperations() throws ParseException {
        assertEquals("10", parser.parse("4+3*2").evaluate());
    }

    @Test
    public void testEvaluateVariable() throws ParseException {
        functorSymbolTable.addValue("variable", 5);
        assertEquals("5", parser.parse("variable").evaluate());
    }

    @Test
    public void testEvaluateStorageVariable() throws ParseException, BotErrorException {
        functorSymbolTable.addValue("variable", new IntegerStorageValue(
                StorageValue.UNREGISTERED_ID, StorageValue.GLOBAL_USER, "variable", 5));
        assertEquals("5", parser.parse("variable").evaluate());
    }

    @Test
    public void testPreIncrementStorageVariable() throws ParseException, BotErrorException {
        IntegerStorageValue value =
                new IntegerStorageValue(StorageValue.UNREGISTERED_ID, StorageValue.GLOBAL_USER, "variable", 5);
        functorSymbolTable.addValue("variable", value);

        doAnswer((invocationOnMock) -> {
            value.setValue(6);
            return value;
        }).when(homeEditor).incrementStorageValue("variable");

        assertEquals("6", parser.parse("++variable").evaluate());
    }

    @Test
    public void testPostIncrementStorageVariable() throws ParseException, BotErrorException {
        IntegerStorageValue value =
                new IntegerStorageValue(StorageValue.UNREGISTERED_ID, StorageValue.GLOBAL_USER, "variable", 5);
        functorSymbolTable.addValue("variable", value);

        doAnswer((invocationOnMock) -> {
            value.setValue(6);
            return value;
        }).when(homeEditor).incrementStorageValue("variable");

        assertEquals("5", parser.parse("variable++").evaluate());
    }

    @Test
    public void testExpressionWithWhitespace() throws ParseException {
        assertEquals("10", parser.parse("4 +\t3 *\t2").evaluate());
    }

    @Test
    public void testBookExpression() throws ParseException, BotErrorException {
        List<Statement> statements = Collections.singletonList(new Statement(Statement.UNREGISTERED_ID, TEXT));
        Book book = new Book(Book.UNREGISTERED_ID, "name", statements);
        functorSymbolTable.addFunctor("variable", () -> book);
        assertEquals(TEXT, parser.parse("variable").evaluate());
    }

    @Test
    public void testFunctionExpression() throws ParseException {
        Function<Integer, Integer> f = (a) -> a + 1;
        functorSymbolTable.addValue("function", f);
        assertEquals("3", parser.parse("function(2)").evaluate());
    }

    @Test(expected = ParseException.class)
    public void testFunctionExpressionWithBadParameterType() throws ParseException {
        Function<String, Integer> f = String::length;
        functorSymbolTable.addValue("function", f);
        assertEquals("5", parser.parse("function(2)").evaluate());
    }

    @Test
    public void testDurationExpression() throws ParseException {
        functorSymbolTable.addValue("duration", Duration.ofSeconds(2));
        assertEquals("2 seconds", parser.parse("duration").evaluate());
    }

    private static class Container {
        @Getter @Setter
        private int field;
    }

    @Test
    public void testMemberAccess() throws ParseException {
        Container container = new Container();
        container.setField(5);
        functorSymbolTable.addValue("container", container);
        assertEquals("5", parser.parse("container.field").evaluate());
    }
}