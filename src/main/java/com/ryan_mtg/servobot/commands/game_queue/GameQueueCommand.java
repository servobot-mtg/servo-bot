package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.user.User;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameQueueCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.GAME_QUEUE_COMMAND_TYPE;

    private static Logger LOGGER = LoggerFactory.getLogger(GameQueueCommand.class);

    @Getter
    private int gameQueueId;

    public GameQueueCommand(final int id, final CommandSettings commandSettings, final int gameQueueId) {
        super(id, commandSettings);
        this.gameQueueId = gameQueueId;
    }

    @Override
    public CommandType getType() {
        return TYPE;
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
                    MessageCommand.say(event, responseMessage);
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
