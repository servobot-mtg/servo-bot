package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;

import java.util.Scanner;
import java.util.regex.Pattern;

public class AddCommand extends MessageCommand {
    public static final int TYPE = 5;
    private static final Pattern commandPattern = Pattern.compile("\\w+");

    public AddCommand(final int id, final int flags, final Permission permission) {
        super(id, flags, permission);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitAddCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Scanner scanner = new Scanner(arguments);

        String firstToken = scanner.next();
        if (firstToken.length() >= 1 && firstToken.charAt(0) != '!') {
            throw new BotErrorException("Commands must start with a '!'.");
        }

        if (firstToken.length() <= 1) {
            throw new BotErrorException("No command to add.");
        }

        String command = firstToken.substring(1);

        scanner.useDelimiter("\\z");

        if (!commandPattern.matcher(command).matches()) {
            throw new BotErrorException(String.format("%s doesn't look like a command.", command));
        }

        if (!scanner.hasNext()) {
            throw new BotErrorException(String.format("%s doesn't do anything.", command));
        }

        String text = scanner.next().trim();

        if (text.isEmpty()) {
            throw new BotErrorException(String.format("%s doesn't do anything.", command));
        }

        HomeEditor homeEditor = event.getHomeEditor();
        homeEditor.addCommand(command,
                new TextCommand(Command.UNREGISTERED_ID, DEFAULT_FLAGS, Permission.ANYONE, text));

        String finalText = String.format("Command %s added.", command);
        MessageCommand.say(event, finalText);
    }
}
