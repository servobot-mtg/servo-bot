package com.ryan_mtg.servobot.mpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PlayerSet {
    private Map<String, Player> byNameMap;

    public PlayerSet(final Map<String, Player> byNameMap) {
        this.byNameMap = byNameMap;
    }

    public Collection<Player> getPlayers() {
        List<Player> players = new ArrayList<>(byNameMap.values());
        Collections.sort(players, PlayerSet::compare);
        return players;
    }

    public static int compare(Player a, Player b) {
        return b.getStartPoints() - a.getStartPoints();
    }
}