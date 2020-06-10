package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.user.User;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameQueueCommand extends InvokedHomedCommand {
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
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        HomeEditor homeEditor = event.getHomeEditor();
        String arguments = event.getArguments();
        //TODO: Change to use command parser

        switch (arguments.toLowerCase()) {
            case "name":
                homeEditor.setGameQueueName(gameQueueId, "Queue Name");
                return;
            case "start":
                String responseMessage = homeEditor.startGameQueue(gameQueueId, null);
                if (responseMessage != null) {
                    event.say(responseMessage);
                }
                return;
            case "pop":
            case "next":
                User nextPlayer = homeEditor.popGameQueue(gameQueueId);
                String response = String.format("The next player is %s ", nextPlayer.getTwitchUsername());
                event.say(response);
                return;
            case "peek":
            case "playing":
            case "current":
                User currentPlayer = homeEditor.peekGameQueue(gameQueueId);
                response = String.format("The current player is %s ", currentPlayer.getTwitchUsername());
                event.say(response);
                return;
            case "close":
                responseMessage = homeEditor.closeGameQueue(gameQueueId);
                if (responseMessage != null) {
                    event.say(responseMessage);
                }
                return;
            case "stop":
                responseMessage = homeEditor.stopGameQueue(gameQueueId);
                if (responseMessage != null) {
                    event.say(responseMessage);
                }
                return;
            default:
                throw new UserError("Invalid Game Queue Command: " + arguments);
        }
    }
}
