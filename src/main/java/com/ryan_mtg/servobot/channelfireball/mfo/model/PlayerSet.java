package com.ryan_mtg.servobot.channelfireball.mfo.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayerSet implements Iterable<Player> {
    private Map<String, Player> arenaNameMap = new HashMap<>();
    private Map<String, Player> discordNameMap = new HashMap<>();
    private Map<String, Player> shortArenaNameMap = new HashMap<>();

    public Collection<Player> getPlayers() {
        return arenaNameMap.values();
    }

    public Player add(final Player player) {
        arenaNameMap.put(player.getArenaName(), player);
        discordNameMap.put(player.getDiscordName(), player);
        shortArenaNameMap.put(player.getShortArenaName().toLowerCase(), player);
        return player;
    }

    public Player findByArenaName(final String arenaName) {
        if (arenaName.equalsIgnoreCase(Player.BYE.getArenaName())) {
            return Player.BYE;
        }

        if (arenaNameMap.containsKey(arenaName)) {
            return arenaNameMap.get(arenaName);
        }

        String lowerCaseArenaName = arenaName.toLowerCase();
        if (shortArenaNameMap.containsKey(lowerCaseArenaName)) {
            return shortArenaNameMap.get(lowerCaseArenaName);
        }

        if (arenaNameMap.containsKey(lowerCaseArenaName)) {
            return arenaNameMap.get(lowerCaseArenaName);
        }

        return null;
    }

    @Override
    public Iterator<Player> iterator() {
        return discordNameMap.values().iterator();
    }
}
