package com.ryan_mtg.servobot.game.sus;

import com.ryan_mtg.servobot.game.GameState;
import com.ryan_mtg.servobot.game.GameUtils;
import com.ryan_mtg.servobot.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SusGameState implements GameState {
    private final List<User> players = new ArrayList<>();
    private final List<User> imposters = new ArrayList<>();
    private final List<User> crew = new ArrayList<>();
    private final List<SusAction> gameLog = new ArrayList<>();

    private State state;

    public enum State {
        AWAITING_PLAYERS,
        STARTED,
        FINISHED,
    };

    public boolean isImposter(final User player) {
        return hasPlayer(imposters, player);
    }

    public boolean isCrew(final User player) {
        return hasPlayer(crew, player);
    }

    public boolean isAlive(final User player) {
        return isCrew(player) || isImposter(player);
    }

    public boolean isOnCoolDown(final User player) {
        return !gameLog.isEmpty() && GameUtils.isSame(player, gameLog.get(gameLog.size() - 1).getActor());
    }

    public void kill(final User killer, final User victim) {
        gameLog.add(new KillAction(killer, victim));
        if (isCrew(victim)) {
            crew.remove(victim);
        } else if (isImposter(victim)) {
            imposters.remove(victim);
        }
    }

    public boolean hasPlayer(final List<User> list, final User player) {
        for (User user : list) {
            if (GameUtils.isSame(user, player)) {
                return true;
            }
        }
        return false;
    }
}