package com.ryan_mtg.servobot.channelfireball.mfo.model;

import java.util.HashMap;
import java.util.Map;

public class PlayerSet {
    private Map<String, Player> arenaNameMap = new HashMap<>();
    private Map<String, Player> discordNameMap = new HashMap<>();
    private Map<String, Player> shortArenaNameMap = new HashMap<>();

    public Player add(final Player player) {
        arenaNameMap.put(player.getArenaName(), player);
        discordNameMap.put(player.getDiscordName(), player);
        shortArenaNameMap.put(player.getShortArenaName().toLowerCase(), player);
        return player;
    }

    public Player findByArenaName(final String arenaName) {
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
}