package com.ryan_mtg.servobot.game;

import com.ryan_mtg.servobot.user.User;

public interface Game {
    boolean awaitingPlayers();
    boolean hasPlayer(User player);
    void join(User player);
    void start();

    void sendCommand(User player, GameCommand command);
}