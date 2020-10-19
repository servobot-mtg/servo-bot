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
            case "add":
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
            case "ready":
            case "here":
                ready(event, parseResult.getInput());
                return;
            case "last":
            case "lg":
            case "done":
                lg(event, parseResult.getInput());
                return;
            case "move":
            case "position":
                move(event, command, parseResult.getInput());
                break;
            case "rotate":
            case "requeue":
                rotate(event, parseResult.getInput());
                break;
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
            GameQueueUtils.updateMessage(event, gameQueue, message, action, true);
        } else {
            showQueue(event);
        }
    }

    private void showQueue(final CommandInvokedHomeEvent event) throws UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueue gameQueue = gameQueueEditor.getGameQueue(gameQueueId);

        Message previousMessage = gameQueue.getMessage();

        String text = GameQueueUtils.createMessage(gameQueue);

        Message message = event.getChannel().sayAndWait(text);
        message.addEmote(new DiscordEmoji(GameQueueUtils.REFRESH_EMOTE));
        message.addEmote(new DiscordEmoji(GameQueueUtils.DAGGER_EMOTE));
        message.addEmote(new DiscordEmoji(GameQueueUtils.READY_EMOTE));
        message.addEmote(new DiscordEmoji(GameQueueUtils.LG_EMOTE));
        message.addEmote(new DiscordEmoji(GameQueueUtils.LEAVE_EMOTE));
        if (event.getServiceType() == DiscordService.TYPE) {
            gameQueueEditor.setMessage(gameQueue, message);
        }

        if (previousMessage != null) {
            previousMessage.updateText("The game queue has been displayed below.");
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
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.addUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.addUser(gameQueueId, user));
            }
        }
        showOrUpdateQueue(event, action);
    }

    private void ready(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.readyUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.readyUser(gameQueueId, user));
            }
        }
        showOrUpdateQueue(event, action);
    }

    private void lg(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.lgUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.lgUser(gameQueueId, user));
            }
        }
        showOrUpdateQueue(event, action);
    }

    private List<HomedUser> getPlayerList(final ServiceHome serviceHome, final String list) throws UserError {
        List<HomedUser> users = new ArrayList<>();
        for(String name : list.split(",")) {
            users.add(getUser(serviceHome, name));
        }
        return users;
    }

    private void dequeueUser(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();

        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.dequeueUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.dequeueUser(gameQueueId, user));
            }
        }
        showOrUpdateQueue(event, action);
    }

    private void move(final CommandInvokedHomeEvent event, final String command, final String input)
            throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        String trailingInt = extractTrailingInt(input);
        if (Strings.isBlank(trailingInt)) {
            throw new UserError("%s needs a position to move to.", command);
        }

        GameQueueAction action;
        int position = Integer.parseInt(trailingInt);
        if (trailingInt.length() < input.length()) {
            String name = input.substring(0, input.length() - trailingInt.length());
            action = gameQueueEditor.moveUser(gameQueueId, getUser(event.getServiceHome(), name), position);
        } else {
            action = gameQueueEditor.moveUser(gameQueueId, event.getSender().getHomedUser(), position);
        }

        showOrUpdateQueue(event, action);
    }

    private void rotate(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.rotateUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.rotateUser(gameQueueId, user));
            }
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
        text.append("join: enqueue: Adds you to the queue. With arguments, adds the user(s) specified to the queue.\n");
        text.append("ready: Adds you to the game if you are on deck. With arguments, adds the user(s) specified to the game.\n");
        text.append("last: LG: Marks you as being in your last game. With arguments, marks the user(s) specified as being in their last game.\n");
        text.append("rotate: Moves you to the end of the queue. With arguments, moves the user(s) specified to the end of the queue.\n");
        text.append("move: Moves you to the position specified. With more arguments, moves the user specified to the given position.\n");
        text.append("remove: dequeue: Removes you from the game or queue. With arguments, removes the user(s) specified from the queue.\n");
        text.append("reset: clear: Removes everyone from the queue and removes any game code.\n");
        text.append("help: Displays this message.\n");
        text.append("```\n");
        event.getChannel().say(text.toString());
    }

    private HomedUser getUser(final ServiceHome serviceHome, final String name) throws UserError {
        return serviceHome.getUser(name.trim()).getHomedUser();
    }

    private String extractTrailingInt(final String input) {
        if (input == null) {
            return null;
        }
        int trailingCharacters = 0;
        int len = input.length();
        while (trailingCharacters < len && Character.isDigit(input.charAt(len - 1 - trailingCharacters))) {
            trailingCharacters++;
        }
        return input.substring(len - trailingCharacters);
    }
}