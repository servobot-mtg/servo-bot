package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.discord.model.DiscordEmoji;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.editors.GameQueueEditor;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueAction;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.CommandParser;
import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class GameQueueCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.GAME_QUEUE_COMMAND_TYPE;

    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\w+");
    private static final Pattern CODE_PATTERN = Pattern.compile("\\w{6}");
    private static final CommandParser COMMAND_PARSER = new CommandParser(COMMAND_PATTERN);

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

        switch (command.toLowerCase()) {
            case "show":
                showQueue(event);
                return;
            case "server":
            case "code":
                setCode(event, command, parseResult.getInput());
                return;
            case "join":
            case "queue":
            case "enqueue":
            case "enter":
                enqueueUser(event, parseResult.getInput());
                return;
            case "dequeue":
            case "remove":
            case "leave":
            case "exit":
            case "out":
                dequeueUser(event, parseResult.getInput());
                return;
            case "clear":
            case "reset":
                clear(event);
                return;
            case "help":
                help(event);
                return;

            default:
                throw new UserError("Invalid Game Queue Command: " + arguments);
        }
    }

    private void showOrUpdateQueue(final CommandInvokedHomeEvent event, final GameQueueAction action)
            throws BotHomeError, UserError {
        GameQueue gameQueue = event.getGameQueueEditor().getGameQueue(gameQueueId);
        Message message = gameQueue.getMessage();
        if (message != null && !message.isOld()) {
            GameQueueUtils.updateMessage(gameQueue, message);
            switch (action.getEvent()) {
                case CODE_CHANGED:
                    event.say(GameQueueUtils.getCodeMessage(action.getCode(), action.getServer()));
                    break;
                case PLAYERS_QUEUE:
                    event.say(GameQueueUtils.getPlayersQueuedMessage(action.getQueuedPlayers()));
                    break;
                case PLAYERS_LEAVE:
                    event.say(GameQueueUtils.getPlayersDequeuedMessage(action.getDequeuedPlayers()));
                    break;
            }
        } else {
            showQueue(event);
        }
    }

    private void showQueue(final CommandInvokedHomeEvent event) throws UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueue gameQueue = gameQueueEditor.getGameQueue(gameQueueId);

        String text = GameQueueUtils.createMessage(gameQueue);

        Message message = event.getChannel().sayAndWait(text);
        message.addEmote(new DiscordEmoji(GameQueueUtils.DAGGER_EMOTE));
        if (event.getServiceType() == DiscordService.TYPE) {
            gameQueueEditor.setMessage(gameQueue, message);
        }
    }

    private void setCode(final CommandInvokedHomeEvent event, final String command, final String input)
            throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        if (Strings.isBlank(input)) {
            event.say(GameQueueUtils.getCodeMessage(gameQueueEditor.getGameQueue(gameQueueId)));
            return;
        }

        Scanner scanner = new Scanner(input);
        String code = null;
        String server = null;

        while (scanner.hasNext()) {
            String token = scanner.next();
            if (CODE_PATTERN.matcher(token).matches() && !token.equalsIgnoreCase("europe")) {
                code = token;
                continue;
            }

            switch (token.toLowerCase()) {
                case "eu":
                case "europe":
                    server = "EU";
                    continue;
                case "asia":
                    server = "ASIA";
                    continue;
                case "na":
                case "north":
                case "america":
                    server = "NA";
                    continue;
            }
            throw new UserError("Unrecognized %s: %s", command, token);
        }

        GameQueueAction action = null;
        if (code != null && server != null) {
            action = gameQueueEditor.setCodeAndServer(gameQueueId, code, server);
        } else if (code != null) {
            action = gameQueueEditor.setCode(gameQueueId, code);
        } else if (server != null) {
            action = gameQueueEditor.setServer(gameQueueId, server);
        }

        showOrUpdateQueue(event, action);
    }

    private void enqueueUser(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action;
        if (Strings.isBlank(input)) {
            action = gameQueueEditor.addUser(gameQueueId, event.getSender().getHomedUser());
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
            for(HomedUser user : users) {
                gameQueueEditor.addUser(gameQueueId, user);
            }
            action = GameQueueAction.playersQueued(users);
        }
        showOrUpdateQueue(event, action);
    }

    private List<HomedUser> getPlayerList(final ServiceHome serviceHome, final String list) throws UserError {
        List<HomedUser> users = new ArrayList<>();
        for(String name : list.split(",")) {
            users.add(serviceHome.getUser(name.trim()).getHomedUser());
        }
        return users;
    }

    private void dequeueUser(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action;
        if (Strings.isBlank(input)) {
            action = gameQueueEditor.dequeueUser(gameQueueId, event.getSender().getHomedUser());
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
            for(HomedUser user : users) {
                gameQueueEditor.dequeueUser(gameQueueId, user);
            }
            action = GameQueueAction.playersDequeued(users);
        }

        showOrUpdateQueue(event, action);
    }

    private void clear(final CommandInvokedHomeEvent event) throws UserError {
        event.getGameQueueEditor().clear(gameQueueId);
        showQueue(event);
    }

    private void help(final CommandInvokedHomeEvent event) {
        StringBuilder text = new StringBuilder();
        text.append("Command syntax:\n  !").append(event.getCommand()).append(" *command*  [*args*]\n\n");
        text.append("Where *command*  is one of: ```YAML\n");
        text.append("show: Displays the full details of the queue.\n");
        text.append("server: code: Without any arguments, displays the server and code. With arguments, sets the server and/or code.\n");
        text.append("join: enqueue: Adds you to the queue. With arguments, adds the user specified to the queue.\n");
        text.append("remove: dequeue: Removes you from the game or queue. With arguments, removes the user specified from the queue.\n");
        text.append("reset: clear: Removes everyone from the queue and removes any game code.\n");
        text.append("help: Displays this message.\n");
        text.append("```\n");
        event.getChannel().say(text.toString());
    }
}