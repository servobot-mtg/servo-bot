package com.ryan_mtg.servobot.commands.game;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.error.BotError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.game.Game;
import com.ryan_mtg.servobot.game.GameManager;
import com.ryan_mtg.servobot.user.User;
import lombok.Getter;

public class GameCommand extends InvokedCommand {
    public static final CommandType TYPE = CommandType.GAME_COMMAND_TYPE;

    @Getter
    private final int gameType;

    public GameCommand(final int id, final CommandSettings commandSettings, final int gameType) {
        super(id, commandSettings);
        this.gameType = gameType;
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotError, UserError {
        GameManager gameManager = event.getBotEditor().getGameManager(gameType);
        UserError.filter(() -> {
            User player = event.getSender().getUser();
            Game game = gameManager.lookupGame(player);
            if (game == null) {
                gameManager.joinGame(player);
                game = gameManager.lookupGame(player);
                // add fake players
                game.join(event.getBotEditor().getUserById(2));
                game.join(event.getBotEditor().getUserById(5));
                game.join(event.getBotEditor().getUserById(4));
            } else {
                com.ryan_mtg.servobot.game.GameCommand command =
                        new com.ryan_mtg.servobot.game.GameCommand(event.getCommand(), event.getArguments());
                gameManager.sendCommand(player, command);
            }
        });
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitGameCommand(this);
    }
}