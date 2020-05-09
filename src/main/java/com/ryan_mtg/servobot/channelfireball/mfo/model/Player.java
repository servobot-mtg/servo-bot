package com.ryan_mtg.servobot.channelfireball.mfo.model;

import lombok.Getter;

@Getter
public class Player {
    public static final Player BYE = new Player("BYE", "BYE");

    private String arenaName;
    private String discordName;

    private Player(final String arenaName, final String discordName) {
        this.arenaName = arenaName;
        this.discordName = discordName;
    }

    public String getShortArenaName() {
        int hashTagIndex = arenaName.indexOf("#");
        if (hashTagIndex >= 0) {
            arenaName.substring(0, hashTagIndex).toLowerCase();
        }
        return arenaName;
    }

    public static Player createFromMfoName(final String name) {
        int commaIndex = name.indexOf(", ");
        if (commaIndex < 0) {
            return Player.BYE;
        }
        final String arenaName = name.substring(0, commaIndex);
        final String discordName = name.substring(commaIndex + 2);
        return new Player(arenaName,discordName);
    }
}
