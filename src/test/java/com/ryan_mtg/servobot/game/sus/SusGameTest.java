package com.ryan_mtg.servobot.game.sus;

import com.ryan_mtg.servobot.user.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SusGameTest {
    private SusGame game;
    private SusGameManager gameManager;
    private User[] players;

    @Before
    public void setUp() {
        gameManager = mock(SusGameManager.class);
        players = new User[SusGame.POD_SIZE];
        game = new SusGame(gameManager);
        for (int i = 0; i < players.length; i++) {
            players[i] = mock(User.class);
        }
    }

    @Test
    public void testJoinGame() {
        User player = mock(User.class);
        game.join(player);

        assertTrue(game.hasPlayer(player));
        verify(gameManager).respond(eq(player), contains("in the queue"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCantJoinGameReturnsFalse() {
        User player = mock(User.class);
        game.join(player);
        game.join(player);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMessageIsSentToAllPlayersWhenGameStarts() {
        joinAll();

        for (User player : players) {
            verify(gameManager).respond(eq(player), contains("starting"));
        }

        verify(gameManager, times(SusGame.IMPOSTER_COUNT)).respond(any(User.class), contains("imposter"));
        verify(gameManager, times(SusGame.CREW_COUNT)).respond(any(User.class), contains("crew"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMorePlayersThanPodSizeCantJoin() {
        User player = mock(User.class);
        joinAll();
        game.join(player);
    }

    private void joinAll() {
        for (User player : players) {
            game.join(player);
        }
    }
}