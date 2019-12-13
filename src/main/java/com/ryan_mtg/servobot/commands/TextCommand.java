package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.MessageSentEvent;

public class TextCommand extends MessageCommand {
    public static final int TYPE = 1;
    private final String text;

    public TextCommand(final int id, final boolean secure, final Permission permission, final String text) {
        super(id, secure, permission);
        this.text = text;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) {
        String sender = event.getSender().getName();

        String finalText = text.replace("%user%", sender) ;
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
}
