package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.editors.GameQueueEditor;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.CommandParser;
import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class GameQueueCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.GAME_QUEUE_COMMAND_TYPE;

    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\w+");
    private static final Pattern CODE_PATTERN = Pattern.compile("\\w{6}");
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
                enqueueUser(event);
                return;
            case "dequeue":
            case "remove":
            case "leave":
            case "exit":
            case "out":
                dequeueUser(event);
                return;
            case "clear":
            case "reset":
                clear(event);
                return;
            case "help":
                help(event);
                return;

            /*
            case "pop": case "next":
                return;
            case "peek": case "playing": case "current":
                return;
             */
            default:
                throw new UserError("Invalid Game Queue Command: " + arguments);
        }
    }

    private void appendCode(final StringBuilder text, final GameQueue gameQueue) {
        String code = gameQueue.getCode();
        String server = gameQueue.getServer();
        if (code != null) {
            text.append("🔑 **").append(code).append("**");
            if (server != null) {
                text.append(" on 🖥️ ").append(server);
            }
        } else if (server != null) {
            text.append("🖥️ ").append(server);
        } else {
            text.append("No game code set.");
        }
    }

    private void showQueue(final CommandInvokedHomeEvent event) throws UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        GameQueue gameQueue = gameQueueEditor.getGameQueue(gameQueueId);

        StringBuilder text = new StringBuilder();
        text.append("Game Queue for ").append(gameQueue.getGame().getName());

        text.append("\t\t\t");
        appendCode(text, gameQueue);
        text.append("\n\n");

        appendPlayerList(text, gameQueue.getGamePlayers(), "CSS", "Players", "No active game.");
        appendPlayerList(text, gameQueue.getWaitQueue(), "HTTP", "Queue", "No one is waiting.");

        Message message = event.getChannel().sayAndWait(text.toString());
        if (event.getServiceType() == DiscordService.TYPE) {
            gameQueueEditor.setMessage(gameQueue, message);
        }
    }

    private void appendPlayerList(final StringBuilder text, final List<HomedUser> players, final String syntax,
            final String title, final String emptyMessage) {
        if (players.isEmpty()) {
            text.append(emptyMessage).append('\n');
        } else {
            text.append(title).append(" ```").append(syntax).append('\n');
            for (HomedUser user : players) {
                text.append(user.getName()).append('\n');
            }
            text.append("```\n");
        }
        text.append('\n');
    }

    private void setCode(final CommandInvokedHomeEvent event, final String command, final String input)
            throws BotHomeError, UserError {
        GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
        if (Strings.isBlank(input)) {
            StringBuilder text = new StringBuilder();
            appendCode(text, gameQueueEditor.getGameQueue(gameQueueId));
            event.say(text.toString());
            return;
        }

        Scanner scanner = new Scanner(input);
        String code = null;
        String server = null;

        while (scanner.hasNext()) {
            String token = scanner.next();
            if (CODE_PATTERN.matcher(token).matches()) {
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

        if (code != null && server != null) {
            gameQueueEditor.setCodeAndServer(gameQueueId, code, server);
        } else if (code != null) {
            gameQueueEditor.setCode(gameQueueId, code);
        } else if (server != null) {
            gameQueueEditor.setServer(gameQueueId, server);
        }

        showQueue(event);
    }

    private void enqueueUser(final CommandInvokedHomeEvent event) throws UserError {
        event.getGameQueueEditor().addUser(gameQueueId, event.getSender().getHomedUser());
        showQueue(event);
    }

    private void dequeueUser(final CommandInvokedHomeEvent event) throws UserError {
        event.getGameQueueEditor().dequeueUser(gameQueueId, event.getSender().getHomedUser());
        showQueue(event);
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