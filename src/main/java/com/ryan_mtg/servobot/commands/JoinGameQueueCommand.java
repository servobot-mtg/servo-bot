package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.User;

public class JoinGameQueueCommand extends MessageCommand {
    public static final int TYPE = 8;
    private int gameQueueId;

    public JoinGameQueueCommand(final int id, final boolean secure, final Permission permission,
                                final int gameQueueId) {
        super(id, secure, permission);
        this.gameQueueId = gameQueueId;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        User user = event.getSender();
        int position = event.getHomeEditor().joinGameQueue(gameQueueId, user);
        MessageCommand.say(event,String.format("%s joined the queue in position %d", user.getName(), position));
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
        commandVisitor.visitJoinGameQueueCommand(this);
    }
}
