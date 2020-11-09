package com.ryan_mtg.servobot.mpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PlayerSet {
    private Map<String, Player> byNameMap;
    private Map<String, String> aliasMap;

    public PlayerSet(final Map<String, Player> byNameMap, final Map<String, String> aliasMap) {
        this.byNameMap = byNameMap;
        this.aliasMap = aliasMap;
    }

    public Collection<Player> getPlayers() {
        List<Player> players = new ArrayList<>(byNameMap.values());
        Collections.sort(players, PlayerSet::compare);
        return players;
    }

    public Player getByName(final String name) {
        if (!byNameMap.containsKey(name)) {
            return byNameMap.get(aliasMap.get(name));
        }

        return byNameMap.get(name);
    }

    public static int compare(final Player a, final Player b) {
        return b.getStartPoints() - a.getStartPoints();
    }
}