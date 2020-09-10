package com.ryan_mtg.servobot.game.sus;

import com.ryan_mtg.servobot.error.LibraryError;
import com.ryan_mtg.servobot.game.Game;
import com.ryan_mtg.servobot.game.GameCommand;
import com.ryan_mtg.servobot.game.GameManager;
import com.ryan_mtg.servobot.game.GameState;
import com.ryan_mtg.servobot.game.Responder;
import com.ryan_mtg.servobot.user.User;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class SusGameManager implements GameManager {
    @Setter
    private Responder responder;
    private List<Game> activeGames = new ArrayList<>();
    private List<Game> completedGames = new ArrayList<>();

    @Override
    public boolean joinGame(final User player) {
        Game foundGame = lookupGame(player);
        if (foundGame != null) {
            return false;
        }

        for (Game game : activeGames) {
            if (game.awaitingPlayers()) {
                game.join(player);
                return true;
            }
        }

        Game newGame = new SusGame(this, responder);
        newGame.join(player);
        activeGames.add(newGame);
        return true;
    }

    @Override
    public void sendCommand(final User player, final GameCommand command) throws LibraryError {
        Game game = lookupGame(player);
        if (game == null) {
            throw new LibraryError("No active game for %s!", player.getName());
        }

        game.sendCommand(player, command);
    }

    @Override
    public void save(final GameState gameState) {
        //TODO: implement a save game state.
    }

    @Override
    public void completeGame(final Game game) {
        activeGames.remove(game);
        completedGames.add(game);
    }

    @Override
    public int getType() {
        return SusGame.TYPE;
    }

    @Override
    public Game lookupGame(final User player) {
        for (Game game : activeGames) {
            if (game.hasPlayer(player)) {
                return game;
            }
        }

        return null;
    }
}
