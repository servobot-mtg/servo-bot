package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.trigger.CommandEvent;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.commands.hierarchy.UserCommand;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.utility.CommandParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class CommandListener implements EventListener {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);
    private static final Pattern COMMAND_PATTERN = Pattern.compile("!\\w+");
    private static final CommandParser COMMAND_PARSER = new CommandParser(COMMAND_PATTERN);

    private CommandPerformer commandPerformer;
    private CommandTable commandTable;

    public CommandListener(final CommandPerformer commandPerformer, final CommandTable commandTable) {
        this.commandPerformer = commandPerformer;
        this.commandTable = commandTable;
    }

    @Override
    public void onMessage(final MessageSentEvent messageSentEvent) {
        Message message = messageSentEvent.getMessage();
        User sender = messageSentEvent.getSender();
        if (sender.isBot()) {
            return;
        }

        LOGGER.trace("seeing event for " + message.getContent());

        CommandParser.ParseResult parseResult = COMMAND_PARSER.parse(message.getContent());
        switch (parseResult.getStatus()) {
            case NO_COMMAND:
            case COMMAND_MISMATCH:
                return;
        }

        String commandString = parseResult.getCommand().substring(1);
        String arguments = parseResult.getInput();

        Command command = commandTable.getCommand(commandString);

        if (command instanceof MessageCommand) {
            MessageCommand messageCommand = (MessageCommand) command;
            commandPerformer.perform(commandString, arguments, messageSentEvent, messageCommand);
        } else if (command == null) {
            messageSentEvent.getHomeEditor().addSuggestion(commandString);
            LOGGER.warn("Unknown command " + commandString + " for " + messageSentEvent.getSender().getName()
                    + " with arguments " + arguments);
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {
        for (HomeCommand command : commandTable.getCommands(CommandEvent.Type.STREAM_START, HomeCommand.class)) {
            commandPerformer.perform(streamStartEvent, command);
        }
    }

    @Override
    public void onNewUser(final UserEvent newUserEvent) {
        for (UserCommand command : commandTable.getCommands(CommandEvent.Type.NEW_USER, UserCommand.class)) {
            commandPerformer.perform(newUserEvent, command);
        }
    }

    @Override
    public void onRaid(final UserEvent raidEvent) {
        for (UserCommand command : commandTable.getCommands(CommandEvent.Type.RAID, UserCommand.class)) {
            commandPerformer.perform(raidEvent,command);
        }
    }

    @Override
    public void onSubscribe(final UserEvent subscribeEvent) {
        for (UserCommand command : commandTable.getCommands(CommandEvent.Type.SUBSCRIBE, UserCommand.class)) {
            commandPerformer.perform(subscribeEvent, command);
        }
    }

    @Override
    public void onAlert(final AlertEvent alertEvent) {
        LOGGER.info("Performing alert " + alertEvent.getAlertToken());
        for (HomeCommand command :
                commandTable.getCommandsFromAlertToken(alertEvent.getAlertToken(), HomeCommand.class)) {
            commandPerformer.perform(alertEvent,command);
        }
    }
}
