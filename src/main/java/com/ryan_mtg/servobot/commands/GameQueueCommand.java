package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameQueueCommand extends MessageCommand {
    public static final int TYPE = 7;

    private static Logger LOGGER = LoggerFactory.getLogger(GameQueueCommand.class);

    private int gameQueueId;

    public GameQueueCommand(final int id, final int flags, final Permission permission, final int gameQueueId) {
        super(id, flags, permission);
        this.gameQueueId = gameQueueId;
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
        commandVisitor.visitGameQueueCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        HomeEditor homeEditor = event.getHomeEditor();
        //TODO: Check for errors in arguments format

        switch (arguments.toLowerCase()) {
            case "name":
                homeEditor.setGameQueueName(gameQueueId, "Queue Name");
                return;
            case "start":
                String responseMessage = homeEditor.startGameQueue(gameQueueId, null);
                if (responseMessage != null) {
                    event.getChannel().say(responseMessage);
                }
                return;
            case "pop":
            case "next":
                User nextPlayer = homeEditor.popGameQueue(gameQueueId);
                String response = String.format("The next player is %s ", nextPlayer.getTwitchUsername());
                MessageCommand.say(event, response);
                return;
            case "peek":
            case "playing":
            case "current":
                User currentPlayer = homeEditor.peekGameQueue(gameQueueId);
                response = String.format("The current player is %s ", currentPlayer.getTwitchUsername());
                MessageCommand.say(event, response);
                return;
            case "close":
                responseMessage = homeEditor.closeGameQueue(gameQueueId);
                if (responseMessage != null) {
                    MessageCommand.say(event, responseMessage);
                }
                return;
            case "stop":
                responseMessage = homeEditor.stopGameQueue(gameQueueId);
                if (responseMessage != null) {
                    MessageCommand.say(event, responseMessage);
                }
                return;
            default:
                throw new BotErrorException("Invalid Game Queue Command: " + arguments);
        }
    }
}
