package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.editors.GameQueueEditor;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.utility.CommandParser;
import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class GameQueueCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.GAME_QUEUE_COMMAND_TYPE;

    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\w+");
    private static final CommandParser COMMAND_PARSER = new CommandParser(COMMAND_PATTERN);

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
        String arguments = event.getArguments();
        if (Strings.isBlank(arguments)) {
            showQueue(event);
            return;
        }

        CommandParser.ParseResult parseResult = COMMAND_PARSER.parse(arguments);
        String command = parseResult.getCommand();
        switch (parseResult.getStatus()) {
            case NO_COMMAND:
                showQueue(event);
                return;
            case COMMAND_MISMATCH:
                throw new UserError("%s doesn't look like a command.", command);
        }

        HomeEditor homeEditor = event.getHomeEditor();
        switch (command) {
            case "show":
                showQueue(event);
                return;
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

    private void showQueue(final CommandInvokedHomeEvent event) throws UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueue gameQueue = gameQueueEditor.getGameQueue(gameQueueId);

        String text = "The game queue is empty, but has an id of: " + gameQueue.getId();
        Message message = event.getChannel().sayAndWait(text);
        if (event.getServiceType() == DiscordService.TYPE) {
            gameQueueEditor.setMessage(gameQueue, message);
        }
    }
}
