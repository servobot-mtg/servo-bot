package com.ryan_mtg.servobot.game;

import com.ryan_mtg.servobot.error.LibraryError;
import com.ryan_mtg.servobot.user.User;

public interface GameManager {
    int getType();
    void setResponder(Responder responder);

    Game lookupGame(User player);
    boolean joinGame(User player);

    void sendCommand(User player, GameCommand command) throws LibraryError;

    void save(GameState gameState);
    void completeGame(Game Game);
}