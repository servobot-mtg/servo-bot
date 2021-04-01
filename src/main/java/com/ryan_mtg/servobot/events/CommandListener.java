package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.UserHomedCommand;
import com.ryan_mtg.servobot.commands.trigger.CommandEvent;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.utility.CommandParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);

    private final CommandPerformer commandPerformer;
    private final CommandTable commandTable;

    public CommandListener(final CommandPerformer commandPerformer, final CommandTable commandTable) {
        this.commandPerformer = commandPerformer;
        this.commandTable = commandTable;
    }

    @Override
    public void onPrivateMessage(final MessageEvent messageEvent) {}

    @Override
    public void onMessage(final MessageHomeEvent event) {
        Message message = event.getMessage();
        User sender = event.getSender();
        if (sender.isBot()) {
            return;
        }

        LOGGER.trace("seeing event for " + message.getContent());

        CommandParser.ParseResult parseResult = commandPerformer.getCommandParser().parse(message.getContent());
        switch (parseResult.getStatus()) {
            case NO_COMMAND:
            case COMMAND_MISMATCH:
                return;
        }

        String commandString = parseResult.getCommand().substring(1);
        String arguments = parseResult.getInput();

        Command command = commandTable.getCommand(commandString);

        if (command == null) {
            event.getHomeEditor().addSuggestion(commandString);
            LOGGER.warn("Unknown command {} for {} with arguments '{}'.", commandString, sender.getName(), arguments);
        } else {
            CommandInvokedHomeEvent commandInvokedEvent =
                    new MessageInvokedHomeEvent(event, commandString, arguments);
            commandPerformer.perform(commandInvokedEvent, command);
        }
    }

    @Override
    public void onEmoteAdded(final EmoteHomeEvent emoteHomeEvent) {}

    @Override
    public void onEmoteRemoved(final EmoteHomeEvent emoteHomeEvent) {}

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {
        for (HomeCommand command : commandTable.getCommands(CommandEvent.Type.STREAM_START, HomeCommand.class)) {
            commandPerformer.perform(streamStartEvent, command);
        }
    }

    @Override
    public void onNewUser(final UserHomeEvent newUserEvent) {
        for (UserHomedCommand command : commandTable.getCommands(CommandEvent.Type.NEW_USER, UserHomedCommand.class)) {
            commandPerformer.perform(newUserEvent, command);
        }
    }

    @Override
    public void onRaid(final UserHomeEvent raidEvent) {
        for (UserHomedCommand  command : commandTable.getCommands(CommandEvent.Type.RAID, UserHomedCommand.class)) {
            commandPerformer.perform(raidEvent,command);
        }
    }

    @Override
    public void onSubscribe(final UserHomeEvent subscribeEvent) {
        for (UserHomedCommand command : commandTable.getCommands(CommandEvent.Type.SUBSCRIBE, UserHomedCommand.class)) {
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
