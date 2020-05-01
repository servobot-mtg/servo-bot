package com.ryan_mtg.servobot.commands.chat;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.utility.CommandParser;
import com.ryan_mtg.servobot.utility.Strings;

import java.util.regex.Pattern;

public class AddCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.ADD_COMMAND_TYPE;

    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\w+");
    private static final CommandParser COMMAND_PARSER = new CommandParser(COMMAND_PATTERN);

    public AddCommand(final int id, final int flags, final Permission permission) {
        super(id, flags, permission);
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitAddCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        CommandParser.ParseResult parseResult = COMMAND_PARSER.parse(arguments);

        String command = parseResult.getCommand();
        switch (parseResult.getStatus()) {
            case NO_COMMAND:
                throw new BotErrorException("No command to add.");
            case COMMAND_MISMATCH:
                if (!command.startsWith("!")) {
                    throw new BotErrorException("Commands must start with a '!'.");
                }
                throw new BotErrorException(String.format("%s doesn't look like a command.", command));
        }

        String input = parseResult.getInput();
        if (Strings.isBlank(input)) {
            throw new BotErrorException(String.format("%s doesn't do anything.", command));
        }

        HomeEditor homeEditor = event.getHomeEditor();
        boolean added = homeEditor.addCommand(command,
                new TextCommand(Command.UNREGISTERED_ID, DEFAULT_FLAGS, Permission.ANYONE, input));

        if (added) {
            MessageCommand.say(event, String.format("Command %s added.", command));
        } else {
            MessageCommand.say(event, String.format("Command %s modified.", command));
        }
    }
}
