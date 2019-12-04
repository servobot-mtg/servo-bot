package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameQueueCommand extends MessageCommand {
    static Logger LOGGER = LoggerFactory.getLogger(GameQueueCommand.class);
    public static final int TYPE = 7;

    private int gameQueueId;

    public GameQueueCommand(final int id, final boolean secure, final Permission permission, final int gameQueueId) {
        super(id, secure, permission);
        this.gameQueueId = gameQueueId;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return "Queue Command";
    }

    public int getGameQueueId() {
        return gameQueueId;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitGameQueueCommand(this);
    }

    @Override
    public void perform(final Message message, final String arguments) throws BotErrorException {
        HomeEditor homeEditor = message.getHome().getHomeEditor();
        //TODO: Check for errors in arguments format

        switch (arguments.toLowerCase()) {
            case "name":
                homeEditor.setGameQueueName(gameQueueId, "Queue Name");
                return;
            case "start":
                String responseMessage = homeEditor.startGameQueue(gameQueueId, null);
                if (responseMessage != null) {
                    message.getChannel().say(responseMessage);
                }
                return;
            case "pop":
            case "next":
                User nextPlayer = homeEditor.popGameQueue(gameQueueId);
                String response = String.format("The next player is %s ", nextPlayer.getTwitchUsername());
                message.getChannel().say(response);
                return;
            case "peek":
            case "playing":
            case "current":
                User currentPlayer = homeEditor.peekGameQueue(gameQueueId);
                response = String.format("The current player is %s ", currentPlayer.getTwitchUsername());
                message.getChannel().say(response);
                return;
            case "close":
                responseMessage = homeEditor.closeGameQueue(gameQueueId);
                if (responseMessage != null) {
                    message.getChannel().say(responseMessage);
                }
                return;
            case "stop":
                responseMessage = homeEditor.stopGameQueue(gameQueueId);
                if (responseMessage != null) {
                    message.getChannel().say(responseMessage);
                }
                return;
            default:
                throw new BotErrorException("Invalid Game Queue Command: " + arguments);
        }
    }
}
