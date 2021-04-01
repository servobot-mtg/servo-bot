package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.utility.Strings;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlayerSet implements Iterable<Player> {
    private final Map<String, Player> arenaNameMap = new HashMap<>();
    private final Map<String, Player> realNameMap = new HashMap<>();
    private final Map<String, Player> discordNameMap = new HashMap<>();
    private final Map<String, Player> shortArenaNameMap = new HashMap<>();
    private final Set<Player> players = new HashSet<>();

    public Collection<Player> getPlayers() {
        return players;
    }

    public Player add(final Player player) {
        if (player.getRealName() != null) {
            realNameMap.put(player.getRealName(), player);
        }

        if (player.getArenaName() != null) {
            arenaNameMap.put(player.getArenaName(), player);
            shortArenaNameMap.put(player.getShortArenaName().toLowerCase(), player);
        }

        if (player.getDiscordName() != null) {
            discordNameMap.put(player.getDiscordName(), player);
        }

        players.add(player);
        return player;
    }

    public Player merge(final Player player) {
        Player foundPlayer = find(player);
        if (foundPlayer == null) {
            return add(player);
        }

        String arenaName = player.getArenaName();
        if (foundPlayer.getArenaName() == null && arenaName != null) {
            arenaNameMap.put(arenaName, foundPlayer);
            shortArenaNameMap.put(player.getShortArenaName().toLowerCase(), foundPlayer);
        }

        String realName = player.getRealName();
        if (foundPlayer.getRealName() == null && realName != null) {
            realNameMap.put(realName, foundPlayer);
        }

        merge(foundPlayer::getArenaName, foundPlayer::setArenaName, player.getRealName());
        merge(foundPlayer::getDiscordName, foundPlayer::setDiscordName, player.getRealName());
        merge(foundPlayer::getRealName, foundPlayer::setRealName, player.getRealName());
        merge(foundPlayer::getNickName, foundPlayer::setNickName, player.getNickName());
        merge(foundPlayer::getTwitchName, foundPlayer::setTwitchName, player.getTwitchName());
        merge(foundPlayer::getTwitterName, foundPlayer::setTwitterName, player.getTwitterName());
        merge(foundPlayer::getIcon, foundPlayer::setIcon, player.getIcon());

        if (foundPlayer.getLeague() == Player.League.NONE) {
            foundPlayer.setLeague(player.getLeague());
        }
        return foundPlayer;
    }

    public Player find(final Player player) {
        String arenaName = player.getArenaName();
        if (arenaName != null) {
            Player foundPlayer = findByArenaName(arenaName);
            if (foundPlayer != null) {
                return foundPlayer;
            }
        }

        String realName = player.getRealName();
        if (realName != null) {
            if (realNameMap.containsKey(realName)) {
                return realNameMap.get(realName);
            }
        }

        return null;
    }

    public Player findByName(final String name) {
        Player player = findByArenaName(name);
        if (player != null) {
            return player;
        }

        if (realNameMap.containsKey(name)) {
            return realNameMap.get(name);
        }

        return null;
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
        return players.iterator();
    }
}
