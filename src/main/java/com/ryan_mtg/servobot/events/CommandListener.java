package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandEvent;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.HomeCommand;
import com.ryan_mtg.servobot.commands.MessageCommand;
import com.ryan_mtg.servobot.commands.UserCommand;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class CommandListener implements EventListener {
    static Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);
    private CommandTable commandTable;

    public CommandListener(final CommandTable commandTable) {
        this.commandTable = commandTable;
    }

    @Override
    public void onMessage(final MessageSentEvent messageSentEvent) throws BotErrorException {
        Message message = messageSentEvent.getMessage();
        User sender = messageSentEvent.getSender();
        if (sender.isBot()) {
            return;
        }

        LOGGER.trace("seeing event for " + message.getContent());

        Scanner scanner = new Scanner(message.getContent());
        if (!scanner.hasNext()) {
            return;
        }

        String firstToken = scanner.next();
        if (firstToken.charAt(0) != '!' || firstToken.length() <= 1) {
            return;
        }

        String commandString = firstToken.substring(1);

        scanner.useDelimiter("\\z");
        String arguments = scanner.hasNext() ? scanner.next().trim() : null;

        Command command = commandTable.getCommand(commandString);

        if (command instanceof MessageCommand) {
            MessageCommand messageCommand = (MessageCommand) command;
            LOGGER.info("Peforming " + commandString + " for " + sender.getName() + " with arguments " + arguments);

            if (messageCommand.getService(messageSentEvent.getServiceType())) {
                if (messageCommand.hasPermissions(sender)) {
                    messageCommand.perform(messageSentEvent, arguments);
                } else {
                    throw new BotErrorException(
                            String.format("%s is not allowed to %s.", sender.getName(), commandString));
                }
            }
        } else if (command == null){
            messageSentEvent.getHomeEditor().addSuggestion(commandString);
            LOGGER.warn("Unknown command " + command + " for " + messageSentEvent.getSender().getName() + " with arguments " + arguments);
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {
        for (HomeCommand command : commandTable.getCommands(CommandEvent.Type.STREAM_START, HomeCommand.class)) {
            command.perform(streamStartEvent.getHome());
        }
    }

    @Override
    public void onNewUser(final NewUserEvent newUserEvent) throws BotErrorException {
        for (UserCommand command : commandTable.getCommands(CommandEvent.Type.NEW_USER, UserCommand.class)) {
            command.perform(newUserEvent.getHome(), newUserEvent.getUser());
        }
    }

    @Override
    public void onAlert(final AlertEvent alertEvent) {
        LOGGER.info("Performing alert " + alertEvent.getAlertToken());
        for (HomeCommand command :
                commandTable.getCommandsFromAlertToken(alertEvent.getAlertToken(), HomeCommand.class)) {
            try {
                LOGGER.info("Performing command " + command.getId());
                command.perform(alertEvent.getHome());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
