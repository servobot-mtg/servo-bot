package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.editors.StorageValueEditor;
import com.ryan_mtg.servobot.model.parser.ParseException;
import com.ryan_mtg.servobot.model.parser.Parser;
import com.ryan_mtg.servobot.model.scope.Scope;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Event {
    Pattern REPLACEMENT_PATTERN = Pattern.compile("%([^%]*)%");
    BotEditor getBotEditor();
    void setBotEditor(BotEditor botEditor);

    default StorageValueEditor getStorageValueEditor() {
        return getBotEditor().getStorageValueEditor();
    }

    int getServiceType();
    Scope getScope();

    default void say(final Channel channel, final Scope scope, final String text) throws BotHomeError {
        channel.say(evaluate(scope, text, 0));
    }

    default void sendImage(final Channel channel, final String url, final String fileName, final String description)
            throws UserError {
        channel.sendImage(url, fileName, description);
    }

    default String evaluate(final Scope scope, final String text, final int recursionLevel) throws BotHomeError {
        if (recursionLevel >= 10) {
            throw new BotHomeError("Too much recursion!");
        }

        StringBuilder result = new StringBuilder();
        Matcher matcher = REPLACEMENT_PATTERN.matcher(text);
        int currentIndex = 0;

        Parser parser = new Parser(scope, getStorageValueEditor());

        while (matcher.find()) {
            result.append(text, currentIndex, matcher.start());
            String expression = matcher.group(1);

            try {
                String evaluation = parser.parse(expression).evaluate();
                String recursiveEvaluation = evaluate(scope, evaluation, recursionLevel + 1);
                result.append(recursiveEvaluation);
            } catch (ParseException e) {
                throw new BotHomeError("Failed to parse %%%s%%: %s", expression, e.getMessage());
            }

            currentIndex = matcher.end();
        }

        result.append(text.substring(currentIndex));
        return result.toString();
    }
}
