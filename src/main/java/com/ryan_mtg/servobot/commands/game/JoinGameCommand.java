package com.ryan_mtg.servobot.commands.game;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotError;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.game.GameManager;
import com.ryan_mtg.servobot.user.User;
import lombok.Getter;

public class JoinGameCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.JOIN_GAME_COMMAND_TYPE;

    @Getter
    private final int gameType;

    public JoinGameCommand(final int id, final CommandSettings commandSettings, final int gameType) {
        super(id, commandSettings);
        this.gameType = gameType;
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotError, BotHomeError {
        GameManager gameManager = event.getBotEditor().getGameManager(gameType);
        User player = event.getSender().getUser();
        boolean joined = gameManager.joinGame(player);
        if (joined) {
            event.say(String.format("%s has joined a game of %s", event.getUser().getName(), event.getCommand()));
        }
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitJoinGameCommand(this);
    }
}
