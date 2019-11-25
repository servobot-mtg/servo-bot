package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;

import java.util.Scanner;

public class AddCommand extends MessageCommand {
    public static final int TYPE = 5;

    public AddCommand(final int id, final boolean secure, final Permission permission) {
        super(id, secure, permission);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return "Add Command";
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitAddCommand(this);
    }

    @Override
    public void perform(final Message message, final String arguments) {
        Scanner scanner = new Scanner(arguments);

        String firstToken = scanner.next();
        if (firstToken.length() <= 1 || firstToken.charAt(0) != '!') {
            return;
        }

        String command = firstToken.substring(1);

        scanner.useDelimiter("\\z");

        if (!scanner.hasNext()) {
            return;
        }

        String text = scanner.next().trim();

        HomeEditor homeEditor = message.getHome().getHomeEditor();
        homeEditor.addCommand(command, new TextCommand(Command.UNREGISTERED_ID, false, Permission.ANYONE, text));

        String finalText = String.format("Command %s added.", command);
        MessageCommand.say(message, finalText);
    }
}
