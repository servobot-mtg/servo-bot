package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;

import java.util.regex.Pattern;

public class SetArenaUsernameCommand extends InvokedCommand {
    public static final CommandType TYPE = CommandType.SET_ARENA_USERNAME_COMMAND_TYPE;
    private static final Pattern NAME_PATTERN = Pattern.compile(".+#\\d{5}");

    public SetArenaUsernameCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotHomeError, UserError {
        String arguments = event.getArguments();
        if (arguments.length() > 23 + 1 + 5) {
            throw new UserError("The Arena Username is too long");
        }

        if (!NAME_PATTERN.matcher(arguments).matches()) {
            throw new UserError("The Arena Username is improperly formatted");
        }

        event.getBotEditor().setArenaUsername(event.getSender().getId(), arguments);

        event.say("Username added.");
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSetArenaUsernameCommand(this);
    }
}
