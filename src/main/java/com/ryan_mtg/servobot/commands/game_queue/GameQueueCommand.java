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
import com.ryan_mtg.servobot.model.game_queue.AmongUsBehavior;
import com.ryan_mtg.servobot.model.game_queue.Game;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueAction;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.CommandParser;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.TimeZoneDescriptor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameQueueCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.GAME_QUEUE_COMMAND_TYPE;

    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\w+");
    private static final Pattern CODE_PATTERN = Pattern.compile("\\w{6}");
    private static final CommandParser COMMAND_PARSER = new CommandParser(COMMAND_PATTERN);

    @Getter
    private final int gameQueueId;

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

        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueue gameQueue = gameQueueEditor.getGameQueue(gameQueueId);

        GameQueueSubCommand subCommand = getSubCommand(gameQueue, parseResult);
        if (subCommand == null) {
            throw new UserError("Invalid Game Queue Command: " + arguments);
        }

        if (gameQueue.isClosed() && !isAllowedWhenClosed(parseResult.getCommand())) {
            throw new UserError("The queue is closed.");
        } else {
            subCommand.execute(event, parseResult);
        }
    }

    private GameQueueSubCommand getSubCommand(final GameQueue gameQueue, final CommandParser.ParseResult parseResult) {
        String command = parseResult.getCommand();

        switch (command.toLowerCase()) {
            case "close":
            case "stop":
            case "end":
            case "shutdown":
                return (event, parsedCommand) -> closeQueue(event);
            case "show":
                return (event, parsedCommand) -> showQueue(event);
            case "add":
            case "join":
            case "queue":
            case "enqueue":
            case "enter":
                return (event, parsedCommand) -> enqueueUser(event, parseResult.getInput());
            case "dequeue":
            case "remove":
            case "leave":
            case "exit":
            case "out":
                return (event, parsedCommand) -> dequeueUser(event, parseResult.getInput());
            case "note":
            case "remember":
                return (event, parseCommand) -> addNote(event, parseResult.getInput());
            case "clear":
            case "reset":
                return (event, parsedCommand) -> clear(event);
            case "unready":
                return (event, parsedCommand) -> unready(event, parseResult.getInput());
            case "ready":
            case "here":
                return (event, parsedCommand) -> ready(event, parseResult.getInput());
            case "noshow":
                return (event, parsedCommand) -> noShow(event, parseResult.getInput());
            case "cut":
            case "rig":
            case "rigged":
                return (event, parsedCommand) -> cut(event, parseResult.getInput());
            case "last":
            case "lg":
            case "done":
                return (event, parsedCommand) -> lg(event, parseResult.getInput());
            case "lgall":
                return (event, parsedCommand) -> lgAll(event);
            case "permanent":
            case "streaming":
                return (event, parsedCommand) -> permanent(event, parseResult.getInput());
            case "move":
            case "position":
                return (event, parsedCommand) -> move(event, command, parseResult.getInput());
            case "rotatelg":
                return (event, parsedCommand) -> rotateLg(event);
            case "rotate":
            case "requeue":
                return (event, parsedCommand) -> rotate(event, parseResult.getInput());
            case "rsvp":
                return (event, parsedCommand) -> rsvp(event, command, parseResult.getInput());
            case "start":
            case "open":
                return (event, parsedCommand) -> start(event);
            case "schedule":
                return (event, parsedCommand) -> schedule(event, command, parseResult.getInput());
            case "ifneeded":
            case "oncall":
                return (event, parsedCommand) -> onCall(event, parseResult.getInput());
            case "name":
            case "tag":
                return (event, parsedCommand) -> setGamerTag(event, command, parseResult.getInput());
            case "variable":
                return (event, parsedCommand) -> setGamerTagVariable(event, command, parseResult.getInput());
            case "minplayers":
            case "min":
                return (event, parsedCommand) -> setMinimumPlayers(event, command, parseResult.getInput());
            case "maxplayers":
            case "max":
                return (event, parsedCommand) -> setMaximumPlayers(event, command, parseResult.getInput());
            case "help":
            case "playerhelp":
                return (event, parsedCommand) -> help(event, gameQueue.getGame());
            case "modhelp":
            case "mod":
                return (event, parsedCommand) -> modHelp(event, gameQueue.getGame());
        }

        if (gameQueue.getGame() == Game.AMONG_US) {
            switch (command.toLowerCase()) {
                case "server":
                case "code":
                case "version":
                    return (event, parsedCommand) -> setCode(event, command, parseResult.getInput());
                case "proximity":
                    return (event, parsedCommand) -> setProximityServer(event, command, parseResult.getInput());
            }
        }

        if (gameQueue.getGame() == Game.BATTLEGROUNDS) {
            switch (command.toLowerCase()) {
                case "full":
                case "eight":
                case "8":
                    return (event, parsedCommand) -> setLobby(event, 8, 8);
                case "partial":
                case "four":
                case "4":
                    return (event, parsedCommand) -> setLobby(event, 2, 4);
            }
        }

        return null;
    }

    private void closeQueue(final CommandInvokedHomeEvent event) throws BotHomeError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueue gameQueue = gameQueueEditor.getGameQueue(gameQueueId);
        Message message = gameQueue.getMessage();
        gameQueueEditor.closeGameQueue(gameQueueId);
        if (message != null) {
            GameQueueUtils.updateMessage(event, gameQueue, message, GameQueueAction.emptyAction(), true);
        }
        event.say(String.format("The **%s** Game Queue has been closed.", gameQueue.getGame().getName()));
    }

    private void showOrUpdateQueue(final CommandInvokedHomeEvent event, final GameQueueAction action)
            throws BotHomeError, UserError {
        GameQueue gameQueue = event.getGameQueueEditor().getGameQueue(gameQueueId);
        Message message = gameQueue.getMessage();
        if (message != null && !message.isOld()) {
            GameQueueUtils.updateMessage(event, gameQueue, message, action, true);
        } else {
            showQueue(event);
            GameQueueUtils.respondToAction(event, gameQueue, action, false);
        }
    }

    private void showQueue(final CommandInvokedHomeEvent event) {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueue gameQueue = gameQueueEditor.getGameQueue(gameQueueId);

        Message previousMessage = gameQueue.getMessage();

        String text = GameQueueUtils.createMessage(event.getGameQueueEditor(), gameQueue,
                event.getHomeEditor().getTimeZone());

        Message message = event.getChannel().sayAndWait(text);
        if (!gameQueue.isClosed()) {
            message.addEmote(new DiscordEmoji(GameQueueUtils.DAGGER_EMOTE));
            message.addEmote(new DiscordEmoji(GameQueueUtils.ON_CALL_EMOTE));
            message.addEmote(new DiscordEmoji(GameQueueUtils.READY_EMOTE));
            message.addEmote(new DiscordEmoji(GameQueueUtils.LG_EMOTE));
            message.addEmote(new DiscordEmoji(GameQueueUtils.ROTATE_EMOTE));
            message.addEmote(new DiscordEmoji(GameQueueUtils.LEAVE_EMOTE));
        }
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
            event.say(AmongUsBehavior.getCodeMessage(gameQueueEditor.getGameQueue(gameQueueId)));
            return;
        }

        Scanner scanner = new Scanner(input);
        String code = null;
        String server = null;
        Boolean onBeta = null;

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
                case "beta":
                    onBeta = true;
                    continue;
                case "main":
                    onBeta = false;
                    continue;
            }
            throw new UserError("Unrecognized %s: %s", command, token);
        }

        GameQueueAction action = gameQueueEditor.setCode(gameQueueId, code, server, onBeta);
        showOrUpdateQueue(event, action);
    }

    private void setProximityServer(final CommandInvokedHomeEvent event, final String command, final String input)
            throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        if (Strings.isBlank(input)) {
            event.say(GameQueueUtils.getProximityServerMessage(
                    gameQueueEditor.getGameQueue(gameQueueId).getProximityServer()));
            return;
        }

        switch (input.toLowerCase()) {
            case "off":
            case "disable":
                GameQueueAction action = gameQueueEditor.setProximityServer(gameQueueId, null);
                showOrUpdateQueue(event, action);
                return;
        }

        GameQueueAction action = gameQueueEditor.setProximityServer(gameQueueId, input);
        showOrUpdateQueue(event, action);
    }

    private void setGamerTag(final CommandInvokedHomeEvent event, final String command, final String input)
            throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueue gameQueue = gameQueueEditor.getGameQueue(gameQueueId);

        String gamerTagVariable = gameQueue.getGamerTagVariable();
        if (Strings.isBlank(gamerTagVariable)) {
            throw new UserError("This queue does not support gamer tags.");
        }

        if (Strings.isBlank(input)) {
            String gamerTag = gameQueueEditor.getGamerTag(event.getSender().getHomedUser(), gamerTagVariable);
            if (!Strings.isBlank(gamerTag)) {
                event.say(String.format("Your gamer tag is %s.", gamerTag));
            } else {
                event.say(String.format("You do not have a gamer tag set. Use `!%s %s UserName#1234` to set it.",
                        event.getCommand(), command));
            }
            return;
        }

        GameQueueAction action = gameQueueEditor.setGamerTag(gameQueueId, event.getSender().getHomedUser(), input);
        showOrUpdateQueue(event, action);
    }

    private void setGamerTagVariable(final CommandInvokedHomeEvent event, final String command, final String input)
            throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        if (Strings.isBlank(input)) {
            throw new UserError("%s command requires an argument.", command);
        }

        GameQueueAction action = gameQueueEditor.setGamerTagVariable(gameQueueId, input);
        GameQueueUtils.respondToAction(event, gameQueueEditor.getGameQueue(gameQueueId), action, true);
    }

    private void setMinimumPlayers(final CommandInvokedHomeEvent event, final String command, final String input)
            throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        if (Strings.isBlank(input)) {
            event.say(String.format("The minimum number of players is %d." ,
                    gameQueueEditor.getGameQueue(gameQueueId).getMinPlayers()));
            return;
        }

        int minimum = getInteger(command, input);
        GameQueueAction action = gameQueueEditor.setMinimumPlayers(gameQueueId, minimum, true);
        showOrUpdateQueue(event, action);
    }

    private void setMaximumPlayers(final CommandInvokedHomeEvent event, final String command, final String input)
            throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        if (Strings.isBlank(input)) {
            event.say(String.format("The maximum number of players is %d." ,
                    gameQueueEditor.getGameQueue(gameQueueId).getMaxPlayers()));
            return;
        }

        int maximum = getInteger(command, input);
        GameQueueAction action = gameQueueEditor.setMaximumPlayers(gameQueueId, maximum, true);
        showOrUpdateQueue(event, action);
    }

    private void setLobby(final CommandInvokedHomeEvent event, final int minPlayers, final int maxPlayers)
            throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();

        GameQueueAction action = gameQueueEditor.setMinimumPlayers(gameQueueId, minPlayers, false);
        action.merge(gameQueueEditor.setMaximumPlayers(gameQueueId, maxPlayers, false));
        showOrUpdateQueue(event, action);
    }


    private int getInteger(final String command, final String input) throws UserError {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            throw new UserError("The argument to the %s command must be a positive integer.", command);
        }
    }

    private void enqueueUser(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.addUser(gameQueueId, event.getSender().getHomedUser(), null));
        } else {
            String note = null, names = input;
            if (input.contains(":")) {
                int index = input.indexOf(':');
                note = input.substring(index + 1).trim();
                names = input.substring(0, index);
            }

            List<HomedUser> users = getPlayerList(event.getServiceHome(), names, true);
            if (users == null) {
                action.merge(gameQueueEditor.addUser(gameQueueId, event.getSender().getHomedUser(), input));
            } else {
                for(HomedUser user : users) {
                    action.merge(gameQueueEditor.addUser(gameQueueId, user, note));
                }
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
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input, false);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.readyUser(gameQueueId, user));
            }
        }
        showOrUpdateQueue(event, action);
    }

    private void unready(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.unreadyUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input, false);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.unreadyUser(gameQueueId, user));
            }
        }
        showOrUpdateQueue(event, action);
    }

    private void noShow(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            throw new UserError("Who didn't show up?");
        }

        List<HomedUser> users = getPlayerList(event.getServiceHome(), input, false);
        for(HomedUser user : users) {
            action.merge(gameQueueEditor.noShowUser(gameQueueId, user));
        }
        showOrUpdateQueue(event, action);
    }

    private void cut(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.cutUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input, false);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.cutUser(gameQueueId, user));
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
            if (input.equalsIgnoreCase("all")) {
                action.merge(gameQueueEditor.lgAll(gameQueueId));
            } else {
                List<HomedUser> users = getPlayerList(event.getServiceHome(), input, false);
                for(HomedUser user : users) {
                    action.merge(gameQueueEditor.lgUser(gameQueueId, user));
                }
            }
        }
        showOrUpdateQueue(event, action);
    }

    private void addNote(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();

        if (!Strings.isBlank(input) && input.contains(":")) {
            int index = input.indexOf(':');
            String name = input.substring(0, index);
            String note = input.substring(index + 1).trim();
            HomedUser player = getUser(event.getServiceHome(), name);
            GameQueueAction action = gameQueueEditor.addNote(gameQueueId, player, note);
            showOrUpdateQueue(event, action);
        } else {
            GameQueueAction action = gameQueueEditor.addNote(gameQueueId, event.getSender().getHomedUser(), input);
            showOrUpdateQueue(event, action);
        }
    }


    private void lgAll(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = gameQueueEditor.lgAll(gameQueueId);
        showOrUpdateQueue(event, action);
    }

    private void permanent(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.permanentUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input, false);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.permanentUser(gameQueueId, user));
            }
        }
        showOrUpdateQueue(event, action);
    }

    private void onCall(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.onCallUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input, false);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.onCallUser(gameQueueId, user));
            }
        }
        showOrUpdateQueue(event, action);
    }

    private List<HomedUser> getPlayerList(final ServiceHome serviceHome, final String list, final boolean checkName)
            throws UserError {
        if (list.contains(",") || !checkName) {
            List<HomedUser> users = new ArrayList<>();
            for(String name : list.split(",")) {
                users.add(getUser(serviceHome, name));
            }
            return users;
        } else {
            try {
                return Collections.singletonList(getUser(serviceHome, list));
            } catch (UserError e) {
                return null;
            }
        }
    }

    private void dequeueUser(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();

        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.dequeueUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input, false);
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
            if (input.equalsIgnoreCase("lg")) {
                action.merge(gameQueueEditor.rotateLg(gameQueueId));
            } else {
                List<HomedUser> users = getPlayerList(event.getServiceHome(), input, false);
                for(HomedUser user : users) {
                    action.merge(gameQueueEditor.rotateUser(gameQueueId, user));
                }
            }
        }
        showOrUpdateQueue(event, action);
    }

    private void rotateLg(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = gameQueueEditor.rotateLg(gameQueueId);
        showOrUpdateQueue(event, action);
    }

    private static final Pattern TIME_PATTERN =
            Pattern.compile("(?<hour>\\d?\\d):(?<minute>\\d\\d)(?<ampm>\\s*(A|P|a|p)(m|M))?" +
                    "(?<zone>\\s+\\w+)?(?<day>\\s+\\w+)?");

    private void rsvp(final CommandInvokedHomeEvent event, final String command, final String input)
            throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();

        ZonedDateTime rsvpTime = parseTime(event, command, input);

        GameQueueAction action =
                gameQueueEditor.rsvpUser(gameQueueId, event.getSender().getHomedUser(), rsvpTime.toInstant());

        showOrUpdateQueue(event, action);
    }

    private void schedule(final CommandInvokedHomeEvent event, final String command, final String input)
            throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        ZonedDateTime scheduledTime = parseTime(event, command, input);
        GameQueueAction action = gameQueueEditor.schedule(gameQueueId, scheduledTime.toInstant());

        showOrUpdateQueue(event, action);
    }

    private void start(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = gameQueueEditor.start(gameQueueId);
        showOrUpdateQueue(event, action);
    }

    private void clear(final CommandInvokedHomeEvent event) throws UserError {
        event.getGameQueueEditor().clear(gameQueueId);
        showQueue(event);
    }

    private void help(final CommandInvokedHomeEvent event, final Game game) {
        StringBuilder text = new StringBuilder();
        text.append("Command syntax: `!").append(event.getCommand()).append(" command [args]`\n\n");
        text.append("Where *command*  is one of: ```YAML\n");
        text.append("show: Displays the full details of the queue.\n");
        text.append("join: enqueue: Adds you or the user(s) specified to the queue.\n");
        text.append("rsvp: Makes a reservation for you to play at the specified time. The time should be formatted similar to 2:30 PM PT\n");
        text.append("tag: Stores your gamer tag so it makes it easier to add you to the game it is your turn to play.\n");
        game.getGameBehavior().appendHelpMessage(text);
        text.append("\n");
        text.append("help: Displays this message.\n");
        text.append("modHelp: Displays a message with commands for moderators.\n");
        text.append("```\n");
        event.getChannel().say(text.toString());
    }

    private void modHelp(final CommandInvokedHomeEvent event, final Game game) {
        StringBuilder text = new StringBuilder();
        text.append("Command syntax: `!").append(event.getCommand()).append(" command [args]`\n\n");
        text.append("Where *command*  is one of: ```YAML\n");
        text.append("schedule: Sets up a game at the specified time and allows players to queue for it without starting a game.\n");
        text.append("open: start: Opens a closed queue and lets players enter a scheduled game if the minimum count is met.\n");
        text.append("reset: clear: Removes everyone from the queue and removes any game code.\n");
        text.append("close: shutdown: Shuts the queue down. No one can join until it has been reopened.\n");
        text.append("min: minPlayers: Sets the minimum number of players needed to start a game.\n");
        text.append("max: maxPlayers: Sets the maximum number of players that are allowed to be in a game.\n");
        text.append("variable: Sets the name of the gamer tag variable. Setting this enables the queue to remember gamer tags.\n");
        text.append("\n");
        text.append("move: Moves the user specified to the given position in the queue.\n");
        text.append("ready: Sets the user(s) specified as ready to play if they are on deck.\n");
        text.append("unready: Returns the user(s) specified to the queue if they are on deck or in the game.\n");
        text.append("noshow: Removes the user(s) specified from the on deck area and if necessary adds the next person from the queue.\n");
        text.append("cut: Moves the user(s) specified to on deck.\n");
        text.append("remove: dequeue: Removes removes the user(s) specified from the queue.\n");
        text.append("last: LG: Marks the user(s) specified as being in their last game. all is a special user which LGs everyone playing.\n");
        text.append("rotate: Moves the user(s) specified to the end of the queue. lg is a special user which rotates everyone marked LG.\n");
        text.append("streaming: permanent: Marks the user(s) specified as permanent so they are not rotated by rotate all.\n");
        text.append("oncall: Adds a user to the queue marked as on-call, where they are only put into the game if the queue is otherwise empty.\n");
        game.getGameBehavior().appendModHelpMessage(text);
        text.append("\n");
        text.append("help: Displays a message with commands for players.\n");
        text.append("```");
        event.getChannel().say(text.toString());
    }

    private boolean isAllowedWhenClosed(final String command) {
        switch (command.toLowerCase()) {
            case "open":
            case "start":
            case "schedule":
            case "help":
            case "playerhelp":
            case "modhelp":
            case "mod":
                return true;
            default:
                return false;
        }
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

    private ZonedDateTime parseTime(final CommandInvokedHomeEvent event, final String command, final String input)
            throws UserError {
        String format = "%s command requires a time formatted as HH:MM PM <time zone> [day]";
        if (Strings.isBlank(input)) {
            throw new UserError(format, command);
        }

        Matcher matcher = TIME_PATTERN.matcher(input);
        if (!matcher.matches()) {
            throw new UserError(format, command);
        }

        int hour = Integer.parseInt(matcher.group("hour"));
        int minutes = Integer.parseInt(matcher.group("minute"));
        if (minutes >= 60) {
            throw new UserError("Minutes must be less than 60");
        }

        String ampm = matcher.group("ampm");
        if (!Strings.isBlank(ampm)) {
            ampm = ampm.trim();
            if (hour > 12) {
                throw new UserError("Cannot have an hour higher than 12 when supplying {}", ampm);
            }

            if (hour == 12) {
                hour = 0;
            }

            if (ampm.toLowerCase().charAt(0) == 'p') {
                hour += 12;
            }
        } else {
            if (hour > 24) {
                throw new UserError("Cannot have an hour higher than 24", ampm);
            }

            if (hour == 24) {
                hour = 0;
            }
        }

        String timeZone = matcher.group("zone");
        if (Strings.isBlank(timeZone)) {
            timeZone = event.getHomeEditor().getTimeZone();
        } else {
            timeZone = timeZone.trim();
            boolean found = false;
            for (TimeZoneDescriptor descriptor : TimeZoneDescriptor.TIME_ZONES) {
                if (timeZone.equalsIgnoreCase(descriptor.getName()) || timeZone.equalsIgnoreCase(descriptor.getAbbreviation())) {
                    timeZone = descriptor.getValue();
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new UserError("Unknown time zone: %s", timeZone);
            }
        }

        String day = matcher.group("day");
        LocalTime time = LocalTime.of(hour, minutes);
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        if (Strings.isBlank(day)) {
            ZonedDateTime dateTime = ZonedDateTime.of(now.toLocalDate(), time, zoneId);
            if (dateTime.compareTo(now) <= 0) {
                dateTime = dateTime.plusDays(1);
            }
            return dateTime;
        } else {
            day = day.trim();

            DayOfWeek dayOfWeek = getDayOfWeek(day);

            ZonedDateTime dateTime = ZonedDateTime.of(now.toLocalDate(), time, zoneId);

            while (dateTime.compareTo(now) <= 0 || dateTime.getDayOfWeek() != dayOfWeek) {
                dateTime = dateTime.plusDays(1);
            }

            return dateTime;
        }
    }

    private DayOfWeek getDayOfWeek(final String day) throws UserError {
        switch (day.toLowerCase()) {
            case "sun":
            case "sunday":
                return DayOfWeek.SUNDAY;
            case "mon":
            case "monday":
                return DayOfWeek.MONDAY;
            case "tues":
            case "tuesday":
                return DayOfWeek.TUESDAY;
            case "wed":
            case "wednesday":
                return DayOfWeek.WEDNESDAY;
            case "thur":
            case "thursday":
                return DayOfWeek.THURSDAY;
            case "fri":
            case "friday":
                return DayOfWeek.FRIDAY;
            case "sat":
            case "saturday":
                return DayOfWeek.SATURDAY;
            default:
                throw new UserError("%s is not a day of the week.", day);
        }
    }
}