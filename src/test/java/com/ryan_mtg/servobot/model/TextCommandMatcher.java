package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.MessageCommand;
import com.ryan_mtg.servobot.commands.TextCommand;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class TextCommandMatcher extends TypeSafeMatcher<MessageCommand> {
    private final String text;

    public TextCommandMatcher(final String text) {
        this.text = text;
    }

    @Override
    protected boolean matchesSafely(final MessageCommand messageCommand) {
        if(!(messageCommand instanceof TextCommand)) {
            return false;
        }
        TextCommand textCommand = (TextCommand) messageCommand;
        return text.equals(textCommand.getText());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("is a TextCommand with text '").appendText(text).appendText("'");
    }
}
