package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.user.User;

import java.util.List;

public class ShowArenaUsernamesCommand extends MessageCommand {
    public static final int TYPE = 12;

    public ShowArenaUsernamesCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        List<User> users = event.getHomeEditor().getArenaUsers();
        StringBuilder stringBuilder = new StringBuilder();
        for (User user : users) {
            String name = user.getTwitchUsername() != null ? user.getTwitchUsername() : user.getDiscordUsername();
            stringBuilder.append(name).append(": ").append(user.getArenaUsername()).append('\n');
        }
        MessageCommand.say(event, stringBuilder.toString());
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitShowArenaUsernamesCommand(this);
    }
}
