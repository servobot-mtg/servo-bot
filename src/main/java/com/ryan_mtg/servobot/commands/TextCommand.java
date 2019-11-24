package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Message;

public class TextCommand extends MessageCommand {
    public static final int TYPE = 1;
    private final String text;

    public TextCommand(final int id, final boolean secure, final String text) {
        super(id, secure);
        this.text = text;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return "Command to say " + text;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitTextCommand(this);
    }

    @Override
    public void perform(final Message message, final String arguments) {
        String author = message.getSender().getName();

        String finalText = text.replace("%user%", author) ;
        MessageCommand.say(message, finalText);
    }

    public String getText() {
        return text;
    }
}
