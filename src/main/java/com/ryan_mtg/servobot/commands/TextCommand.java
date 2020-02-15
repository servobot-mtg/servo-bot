package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.parser.ParseException;
import com.ryan_mtg.servobot.model.parser.Parser;
import com.ryan_mtg.servobot.model.scope.MessageSentSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextCommand extends MessageCommand {
    public static final int TYPE = 1;
    public static final int MAX_TEXT_SIZE = CommandRow.MAX_STRING_SIZE;

    private static Pattern REPLACEMENT_PATTERN = Pattern.compile("%([^%]*)%");
    private final String text;

    public TextCommand(final int id, final int flags, final Permission permission, final String text)
            throws BotErrorException {
        super(id, flags, permission);
        this.text = text;
        if (text.length() > MAX_TEXT_SIZE) {
            throw new BotErrorException("Command text too long.");
        }
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        String finalText = evaluate(event, arguments);
        MessageCommand.say(event, finalText);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitTextCommand(this);
    }

    public String getText() {
        return text;
    }

    private String evaluate(final MessageSentEvent event, final String input) throws BotErrorException {
        StringBuilder result = new StringBuilder();
        Matcher matcher = REPLACEMENT_PATTERN.matcher(text);
        int currentIndex = 0;

        HomeEditor homeEditor = event.getHomeEditor();
        Scope scope = new Scope(homeEditor.getScope(), new MessageSentSymbolTable(event, input));
        Parser parser = new Parser(scope, homeEditor);

        while (matcher.find()) {
            result.append(text.substring(currentIndex, matcher.start()));
            String expression = matcher.group(1);

            try {
                result.append(parser.parse(expression));
            } catch (ParseException e) {
                throw new BotErrorException(String.format("Failed to parse %%%s%%: %s", expression, e.getMessage()));
            }

            currentIndex = matcher.end();
        }

        result.append(text.substring(currentIndex));
        return result.toString();
    }
}
