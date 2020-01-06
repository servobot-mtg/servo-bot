package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextCommand extends MessageCommand {
    public static final int TYPE = 1;
    private static final String SHOW_COMMAND = "show ";
    private static final String INCREMENT_COMMAND = "increment ";
    private final String text;
    Pattern replacementPattern = Pattern.compile("%([^%]*)%");

    public TextCommand(final int id, final boolean secure, final Permission permission, final String text) {
        super(id, secure, permission);
        this.text = text;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        String finalText = evaluate(event);
        MessageCommand.say(event, finalText);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitTextCommand(this);
    }

    public String getText() {
        return text;
    }

    private String evaluate(final MessageSentEvent event) throws BotErrorException {
        StringBuilder result = new StringBuilder();
        Matcher matcher = replacementPattern.matcher(text);
        int currentIndex = 0;

        HomeEditor homeEditor = event.getHomeEditor();

        while (matcher.find()) {
            result.append(text.substring(currentIndex, matcher.start()));

            String replacement = matcher.group(1);
            if (replacement.equals("user")) {
                result.append(event.getSender().getName());
            } else if(replacement.startsWith(SHOW_COMMAND)) {
                String variable = replacement.substring(SHOW_COMMAND.length());
                result.append(homeEditor.getStorageValue(variable).getValue());
            } else if(replacement.startsWith(INCREMENT_COMMAND)) {
                String variable = replacement.substring(INCREMENT_COMMAND.length());
                result.append(homeEditor.incrementStorageValue(variable).getValue());
            } else {
                throw new BotErrorException("Unknown expression " + replacement);
            }

            currentIndex = matcher.end();
        }

        result.append(text.substring(currentIndex));
        return result.toString();
    }
}
