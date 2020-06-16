package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.utility.Strings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    public Player merge(final Player player) {
        Player foundPlayer = findByArenaName(player.getArenaName());
        if (foundPlayer == null) {
            return add(player);
        }

        merge(foundPlayer::getRealName, foundPlayer::setRealName, player.getRealName());
        merge(foundPlayer::getNickName, foundPlayer::setNickName, player.getNickName());
        merge(foundPlayer::getTwitchName, foundPlayer::setTwitchName, player.getTwitchName());
        merge(foundPlayer::getTwitterName, foundPlayer::setTwitterName, player.getTwitterName());
        return foundPlayer;
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

    private void merge(final Supplier<String> getter, final Consumer<String> setter, final String otherName) {
        if (Strings.isBlank(getter.get())) {
            setter.accept(otherName);
        }
    }

    private String merge(final String name, final String otherName) {
        if (Strings.isBlank(name)) {
            return otherName;
        }
        return name;
    }

    @Override
    public Iterator<Player> iterator() {
        return discordNameMap.values().iterator();
    }
}
