package com.ryan_mtg.servobot.discord.commands;

import net.dv8tion.jda.api.entities.Message;

public class TextCommand extends MessageCommand {
    public static final int TYPE = 1;
    private final String text;

    public TextCommand(final int id, final String text) {
        super(id);
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
        String author = message.getAuthor().getName();

        String finalText = text.replace("%user%", author) ;
        MessageCommand.say(message, finalText);
    }

    public String getText() {
        return text;
    }
}
