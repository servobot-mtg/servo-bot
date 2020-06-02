package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import com.ryan_mtg.servobot.utility.CommandParser;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreCommand extends MessageCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreCommand.class);
    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\w+");
    private static final Pattern UPDATE_ARGUMENTS_PATTERN = Pattern.compile("(.*) ((\\+|-)?\\d+)");
    private static final CommandParser COMMAND_PARSER = new CommandParser(COMMAND_PATTERN);

    @Getter
    private final String gameName;

    @Getter
    private final String scoreVariable;

    public ScoreCommand(final int id, final CommandSettings commandSettings, final String gameName,
            final String scoreVariable) throws BotErrorException {
        super(id, commandSettings);
        this.gameName = gameName;
        this.scoreVariable = scoreVariable;

        Validation.validateStringLength(gameName, Validation.MAX_TEXT_LENGTH, "Game name");
        Validation.validateStringLength(scoreVariable, Validation.MAX_STORAGE_NAME_LENGTH, "Score variable name");
    }

    @Override
    public CommandType getType() {
        return CommandType.SCORE_COMMAND_TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitScoreCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        if (Strings.isBlank(arguments)) {
            printScores(event);
            return;
        }

        CommandParser.ParseResult parseResult = COMMAND_PARSER.parse(arguments);

        String commandName = parseResult.getCommand();
        switch (parseResult.getStatus()) {
            case NO_COMMAND:
                printScores(event);
                return;
            case COMMAND_MISMATCH:
                throw new BotErrorException(
                        String.format("%s isn't properly formatted. Must be score(s), reset, or update.", commandName));
        }

        switch (commandName) {
            case "scores":
                printScores(event);
                return;
            case "score":
                printScore(event);
                return;
            case "reset":
                if (!hasPermissions(event.getSender(), Permission.MOD)) {
                    throw new BotErrorException(String.format("%s is not allowed to reset scores.",
                            event.getSender().getName()));
                }

                resetScores(event, parseResult.getInput());
                return;
            case "update":
                if (!hasPermissions(event.getSender(), Permission.MOD)) {
                    throw new BotErrorException(String.format("%s is not allowed to update scores.",
                            event.getSender().getName()));
                }

                updateScore(event, parseResult.getInput());
                return;
            default:
                throw new BotErrorException(
                        String.format("%s isn't valid. Must be score(s), reset, or update.", commandName));
        }
    }

    void printScores(final MessageSentEvent event) throws BotErrorException {
        Home home = event.getHome();
        HomeEditor homeEditor = home.getHomeEditor();
        List<StorageValue> storageValues = homeEditor.getAllUsersStorageValues(scoreVariable);

        StringBuilder message = new StringBuilder();
        if (!storageValues.isEmpty()) {
            message.append(gameName).append(" Scores:\n");
            for(StorageValue storageValue : storageValues) {
                String userName = homeEditor.getUserById(storageValue.getUserId()).getName();
                message.append(String.format("  %s: %s\n", userName, storageValue.getValue()));
            }
        } else {
            message.append("There are no scores currently.");
        }

        MessageCommand.say(event, message.toString());
    }

    void printScore(final MessageSentEvent event) throws BotErrorException {
        HomeEditor homeEditor = event.getHome().getHomeEditor();
        User sender = event.getSender();
        StorageValue storageValue = homeEditor.getStorageValue(sender.getHomedUser().getId(), scoreVariable, 0);

        MessageCommand.say(event, String.format("%s's score is %s.", sender.getName(), storageValue.getValue()));
    }

    void updateScore(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Matcher argumentsMatcher = UPDATE_ARGUMENTS_PATTERN.matcher(arguments);

        if (!argumentsMatcher.matches()) {
            throw new BotErrorException("Command must be: update <user> <score>");
        }

        String userName = argumentsMatcher.group(1);
        int score = Integer.parseInt(argumentsMatcher.group(2));

        Home home = event.getHome();
        HomeEditor homeEditor = home.getHomeEditor();
        User user = home.getUser(userName);
        StorageValue storageValue =
                homeEditor.increaseStorageValue(user.getHomedUser().getId(), scoreVariable, score, 0);

        MessageCommand.say(event, String.format("%s's score is %s.", user.getName(), storageValue.getValue()));
    }

    void resetScores(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Home home = event.getHome();
        HomeEditor homeEditor = home.getHomeEditor();
        if (Strings.isBlank(arguments)) {
            homeEditor.removeStorageVariables(scoreVariable);
            MessageCommand.say(event, String.format("All %s scores reset.", gameName));
            return;
        }

        User user = home.getUser(arguments);
        homeEditor.removeStorageVariable(user.getHomedUser().getId(), scoreVariable);
        MessageCommand.say(event, String.format("%s score for %s reset.", gameName, user.getName()));
    }
}