package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;

import java.util.regex.Pattern;

public class SetArenaUsernameCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.SET_ARENA_USERNAME_COMMAND_TYPE;
    private static final Pattern NAME_PATTERN = Pattern.compile(".+#\\d{5}");

    public SetArenaUsernameCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        if (arguments.length() > 23 + 1 + 5) {
            throw new BotErrorException("The Arena Username is too long");
        }

        if (!NAME_PATTERN.matcher(arguments).matches()) {
            throw new BotErrorException("The Arena Username is improperly formatted");
        }

        event.getBotEditor().setArenaUsername(event.getSender().getHomedUser().getId(), arguments);

        MessageCommand.say(event, "Username added.");
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
