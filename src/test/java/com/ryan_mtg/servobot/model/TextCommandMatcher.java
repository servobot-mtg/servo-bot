package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.commands.chat.TextCommand;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class TextCommandMatcher extends TypeSafeMatcher<InvokedCommand> {
    private final String text;

    public TextCommandMatcher(final String text) {
        this.text = text;
    }

    @Override
    protected boolean matchesSafely(final InvokedCommand invokedCommand) {
        if(!(invokedCommand instanceof TextCommand)) {
            return false;
        }
        TextCommand textCommand = (TextCommand) invokedCommand;
        return text.equals(textCommand.getText());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("is a TextCommand with text '").appendText(text).appendText("'");
    }
}
