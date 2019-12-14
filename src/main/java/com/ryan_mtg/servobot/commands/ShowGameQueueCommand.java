package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;

public class ShowGameQueueCommand extends MessageCommand {
    public static final int TYPE = 9;
    private int gameQueueId;

    public ShowGameQueueCommand(final int id, final boolean secure, final Permission permission,
                                final int gameQueueId) {
        super(id, secure, permission);
        this.gameQueueId = gameQueueId;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        String response = event.getHomeEditor().showGameQueue(gameQueueId);
        MessageCommand.say(event, response);
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
        commandVisitor.visitShowGameQueueCommand(this);
    }
}