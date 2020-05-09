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
        shortArenaNameMap.put(player.getShortArenaName(), player);
        return player;
    }
}
