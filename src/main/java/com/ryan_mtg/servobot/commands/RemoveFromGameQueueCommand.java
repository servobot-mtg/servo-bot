package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;

public class RemoveFromGameQueueCommand extends MessageCommand {
    public static final int TYPE = 10;
    private int gameQueueId;

    public RemoveFromGameQueueCommand(final int id, final boolean secure, final Permission permission,
                                final int gameQueueId) {
        super(id, secure, permission);
        this.gameQueueId = gameQueueId;
    }

    @Override
    public void perform(final Message message, final String arguments) throws BotErrorException {
        User user = message.getSender();
        message.getHome().getHomeEditor().removeFromGameQueue(gameQueueId, user);
        message.getChannel().say(String.format("%s removed from the queue", user.getName()));
    }

    @Override
    public int getType() {
        return TYPE;
    }

    public int getGameQueueId() {
        return gameQueueId;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitRemoveFromGameQueueCommand(this);
    }
}
