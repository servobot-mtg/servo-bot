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
import com.ryan_mtg.servobot.utility.TimeZoneDescriptor;
import lombok.Getter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
            case "version":
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
            case "unready":
                unready(event, parseResult.getInput());
                return;
            case "ready":
            case "here":
                ready(event, parseResult.getInput());
                return;
            case "cut":
            case "rig":
            case "rigged":
                cut(event, parseResult.getInput());
                return;
            case "last":
            case "lg":
            case "done":
                lg(event, parseResult.getInput());
                return;
            case "lgall":
                lgAll(event);
                return;
            case "permanent":
            case "streaming":
                permanent(event, parseResult.getInput());
                return;
            case "move":
            case "position":
                move(event, command, parseResult.getInput());
                break;
            case "rotatelg":
                rotateLg(event);
                break;
            case "rotate":
            case "requeue":
                rotate(event, parseResult.getInput());
                break;
            case "rsvp":
                rsvp(event, command, parseResult.getInput());
                break;
            case "start":
                start(event);
                break;
            case "schedule":
                schedule(event, command, parseResult.getInput());
                break;
            case "ifneeded":
            case "oncall":
                onCall(event, parseResult.getInput());
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
            GameQueueUtils.updateMessage(event, gameQueue, message, action, true);
        } else {
            showQueue(event);
        }
    }

    private void showQueue(final CommandInvokedHomeEvent event) throws UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueue gameQueue = gameQueueEditor.getGameQueue(gameQueueId);

        Message previousMessage = gameQueue.getMessage();

        String text = GameQueueUtils.createMessage(gameQueue, event.getHomeEditor().getTimeZone());

        Message message = event.getChannel().sayAndWait(text);
        message.addEmote(new DiscordEmoji(GameQueueUtils.DAGGER_EMOTE));
        message.addEmote(new DiscordEmoji(GameQueueUtils.ON_CALL_EMOTE));
        message.addEmote(new DiscordEmoji(GameQueueUtils.READY_EMOTE));
        message.addEmote(new DiscordEmoji(GameQueueUtils.LG_EMOTE));
        message.addEmote(new DiscordEmoji(GameQueueUtils.ROTATE_EMOTE));
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

    private void unready(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.unreadyUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.unreadyUser(gameQueueId, user));
            }
        }
        showOrUpdateQueue(event, action);
    }

    private void cut(final CommandInvokedHomeEvent event, final String input) throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueueAction action = GameQueueAction.emptyAction();
        if (Strings.isBlank(input)) {
            action.merge(gameQueueEditor.cutUser(gameQueueId, event.getSender().getHomedUser()));
        } else {
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
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
                List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
                for(HomedUser user : users) {
                    action.merge(gameQueueEditor.lgUser(gameQueueId, user));
                }
            }
        }
        showOrUpdateQueue(event, action);
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
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
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
            List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
            for(HomedUser user : users) {
                action.merge(gameQueueEditor.onCallUser(gameQueueId, user));
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
            if (input.equalsIgnoreCase("lg")) {
                action.merge(gameQueueEditor.rotateLg(gameQueueId));
            } else {
                List<HomedUser> users = getPlayerList(event.getServiceHome(), input);
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
            Pattern.compile("(?<hour>\\d?\\d):(?<minute>\\d\\d)(?<ampm>\\s*(A|P|a|p)(m|M))?(?<zone>\\s+\\w+(\\s+\\w+)?)?");

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

    private void help(final CommandInvokedHomeEvent event) {
        StringBuilder text = new StringBuilder();
        text.append("Command syntax:\n  !").append(event.getCommand()).append(" *command*  [*args*]\n\n");
        text.append("Where *command*  for players is one of: ```YAML\n");
        text.append("show: Displays the full details of the queue.\n");
        text.append("code: server: version: Without any arguments, displays the code and server. With arguments, sets the server, code, and/or version.\n");
        text.append("join: enqueue: Adds you or the user(s) specified to the queue.\n");
        text.append("rsvp: Makes a reservation for you to play at the specified time. The time should be formatted similar to 2:30 PM PT\n");
        text.append("help: Displays this message.\n");
        text.append("```\n");
        text.append("For mods: ```YAML\n");
        text.append("move: Moves you or the user specified to the given position in the queue.\n");
        text.append("ready: Adds you or the user(s) specified to the game if they are on deck.\n");
        text.append("unready: Returns you or the users(s) specified to the queue if they are on deck or in the game.\n");
        text.append("cut: Moves you or the user(s) specified to the deck.\n");
        text.append("remove: dequeue: Removes you from the game or queue. With arguments, removes the user(s) specified from the queue.\n");
        text.append("last: LG: Marks you or the user(s) specified as being in their last game. all is a special user which LGs everyone playing.\n");
        text.append("rotate: Moves you or the user(s) specified to the end of the queue. lg is a special user which rotates everyone marked LG.\n");
        text.append("reset: clear: Removes everyone from the queue and removes any game code.\n");
        text.append("```");
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

    private ZonedDateTime parseTime(final CommandInvokedHomeEvent event, final String command, final String input)
            throws UserError {
        if (Strings.isBlank(input)) {
            throw new UserError("%s command requires a time formatted as HH:MM PM <time zone>", command);
        }

        Matcher matcher = TIME_PATTERN.matcher(input);
        if (!matcher.matches()) {
            throw new UserError("%s command requires a time formatted as HH:MM PM <time zone>", command);
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
                throw new UserError("Unknown time zone: {}", timeZone);
            }
        }

        LocalTime time = LocalTime.of(hour, minutes);

        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime dateTime = ZonedDateTime.of(now.toLocalDate(), time, zoneId);
        if (dateTime.compareTo(now) <= 0) {
            dateTime = dateTime.plusDays(1);
        }
        return dateTime;
    }
}