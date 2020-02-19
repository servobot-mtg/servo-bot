package com.ryan_mtg.servobot.model.parser;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.scope.FunctorSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import org.junit.Before;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.mockHomeEditor;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;

public class ParserTest {
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
        assertEquals(1, parser.parse("1"));
    }

    @Test
    public void testParenthesisExpression() throws ParseException {
        assertEquals(2, parser.parse("(2)"));
    }

    @Test
    public void testAdditionExpression() throws ParseException {
        assertEquals(3, parser.parse("1+2"));
    }

    @Test
    public void testMultiplicationExpression() throws ParseException {
        assertEquals(6, parser.parse("2*3"));
    }

    @Test
    public void testOrderOfOperations() throws ParseException {
        assertEquals(10, parser.parse("4+3*2"));
    }

    @Test
    public void testEvaluateVariable() throws ParseException {
        functorSymbolTable.addFunctor("variable", () -> 5);
        assertEquals(5, parser.parse("variable"));
    }

    @Test
    public void testEvaluateStorageVariable() throws ParseException, BotErrorException {
        StorageValue value =
                new IntegerStorageValue(StorageValue.UNREGISTERED_ID, StorageValue.GLOBAL_USER, "variable", 5);
        functorSymbolTable.addFunctor("variable", () -> value);
        assertEquals(5, parser.parse("variable"));
    }

    @Test
    public void testPreIncrementStorageVariable() throws ParseException, BotErrorException {
        IntegerStorageValue value =
                new IntegerStorageValue(StorageValue.UNREGISTERED_ID, StorageValue.GLOBAL_USER, "variable", 5);
        functorSymbolTable.addFunctor("variable", () -> value);

        doAnswer((invocationOnMock) -> {
            value.setValue(6);
            return value;
        }).when(homeEditor).incrementStorageValue("variable");

        assertEquals(6, parser.parse("++variable"));
    }

    @Test
    public void testPostIncrementStorageVariable() throws ParseException, BotErrorException {
        IntegerStorageValue value =
                new IntegerStorageValue(StorageValue.UNREGISTERED_ID, StorageValue.GLOBAL_USER, "variable", 5);
        functorSymbolTable.addFunctor("variable", () -> value);

        doAnswer((invocationOnMock) -> {
            value.setValue(6);
            return value;
        }).when(homeEditor).incrementStorageValue("variable");

        assertEquals(5, parser.parse("variable++"));
    }


    @Test
    public void testExpressionWithWhitespace() throws ParseException {
        assertEquals(10, parser.parse("4 +\t3 *\t2"));
    }
}