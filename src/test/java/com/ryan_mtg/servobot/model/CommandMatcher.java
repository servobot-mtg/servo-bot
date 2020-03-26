package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.Permission;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class CommandMatcher extends TypeSafeMatcher<Command> {
    private final boolean secure;
    private final Permission permission;

    public CommandMatcher(final boolean secure, final Permission permission) {
        this.secure = secure;
        this.permission = permission;
    }

    @Override
    protected boolean matchesSafely(final Command command) {
        return command.isSecure() == secure && command.getPermission() == permission;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("is a Command with secure ").appendValue(secure)
                   .appendText("and permission").appendValue(permission);
    }
}
