package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import com.ryan_mtg.servobot.utility.CommandParser;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreCommand extends InvokedHomedCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreCommand.class);
    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\w+");
    private static final Pattern UPDATE_ARGUMENTS_PATTERN = Pattern.compile("(.*) ((\\+|-)?\\d+)");
    private static final CommandParser COMMAND_PARSER = new CommandParser(COMMAND_PATTERN);

    @Getter
    private final String gameName;

    @Getter
    private final String scoreVariable;

    public ScoreCommand(final int id, final CommandSettings commandSettings, final String gameName,
            final String scoreVariable) throws UserError {
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
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        String arguments = event.getArguments();
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
                throw new UserError("%s isn't properly formatted. Must be score(s), reset, or update.", commandName);
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
                    throw new UserError("%s is not allowed to reset scores.", event.getSender().getName());
                }

                resetScores(event, parseResult.getInput());
                return;
            case "update":
                if (!hasPermissions(event.getSender(), Permission.MOD)) {
                    throw new UserError("%s is not allowed to update scores.", event.getSender().getName());
                }

                updateScore(event, parseResult.getInput());
                return;
            default:
                throw new UserError("%s isn't valid. Must be score(s), reset, or update.", commandName);
        }
    }

    private void printScores(final CommandInvokedHomeEvent event) throws BotHomeError {
        ServiceHome serviceHome = event.getServiceHome();
        HomeEditor homeEditor = serviceHome.getHomeEditor();
        List<StorageValue> storageValues =
                BotHomeError.filter(() -> homeEditor.getAllUsersStorageValues(scoreVariable));

        StringBuilder message = new StringBuilder();
        if (!storageValues.isEmpty()) {
            message.append(gameName).append(" Scores:\n");
            List<Score> scores = new ArrayList<>();
            for(StorageValue storageValue : storageValues) {
                String userName = homeEditor.getUserById(storageValue.getUserId()).getName();
                scores.add(new Score(userName, (int) storageValue.getValue()));
            }
            for(Score score : scores) {
                message.append(String.format("  %s: %s\n", score.getName(), score.getScore()));
            }
        } else {
            message.append("There are no scores currently.");
        }

        event.say(message.toString());
    }

    @Data @AllArgsConstructor
    private static class Score implements Comparable<Score> {
        private String name;
        private int score;

        @Override
        public int compareTo(final Score score) {
            return this.score - score.score;
        }
    }

    private void printScore(final CommandInvokedHomeEvent event) throws BotHomeError {
        HomeEditor homeEditor = event.getHomeEditor();
        User sender = event.getSender();
        StorageValue storageValue =
                BotHomeError.filter(() -> homeEditor.getStorageValue(sender.getId(), scoreVariable, 0));

        event.say(String.format("%s's score is %s.", sender.getName(), storageValue.getValue()));
    }

    private void updateScore(final CommandInvokedHomeEvent event, final String arguments)
            throws UserError, BotHomeError {
        Matcher argumentsMatcher = UPDATE_ARGUMENTS_PATTERN.matcher(arguments);

        if (!argumentsMatcher.matches()) {
            throw new UserError("Command must be: update <user> <score>");
        }

        String userName = argumentsMatcher.group(1);
        int score = Integer.parseInt(argumentsMatcher.group(2));

        ServiceHome serviceHome = event.getServiceHome();
        HomeEditor homeEditor = serviceHome.getHomeEditor();
        User user = serviceHome.getUser(userName);
        StorageValue storageValue =
                homeEditor.increaseStorageValue(user.getId(), scoreVariable, score, 0);

        event.say(String.format("%s's score is %s.", user.getName(), storageValue.getValue()));
    }

    private void resetScores(final CommandInvokedHomeEvent event, final String arguments)
            throws BotHomeError, UserError {
        ServiceHome home = event.getServiceHome();
        HomeEditor homeEditor = home.getHomeEditor();
        if (Strings.isBlank(arguments)) {
            homeEditor.removeStorageVariables(scoreVariable);
            event.say(String.format("All %s scores reset.", gameName));
            return;
        }

        User user = home.getUser(arguments);
        homeEditor.removeStorageVariable(user.getId(), scoreVariable);
        event.say(String.format("%s score for %s reset.", gameName, user.getName()));
    }
}
