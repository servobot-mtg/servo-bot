package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;

public class DeleteCommand extends MessageCommand{
    public static final int TYPE = 6;

    public DeleteCommand(final int id, final boolean secure, final Permission permission) {
        super(id, secure, permission);
    }

    @Override
    public void perform(final Message message, final String arguments) throws BotErrorException {
        HomeEditor homeEditor = message.getHome().getHomeEditor();

        if (arguments.isEmpty()) {
            throw new BotErrorException("Missing command name.");
        }

        if (arguments.charAt(0) != '!') {
            throw new BotErrorException("Commands must start with '!'");
        }

        String commandName = arguments.substring(1);

        if (commandName.isEmpty()) {
            throw new BotErrorException("Missing command name.");
        }

        homeEditor.deleteCommand(commandName);

        MessageCommand.say(message, String.format("Command %s deleted.", commandName));
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return "Delete Command";
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitDeleteCommand(this);
    }
}
