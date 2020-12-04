package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Player {
    public static final Player BYE = new Player("BYE", "BYE");

    public enum League {
        NONE,
        RIVALS,
        MPL,
    }

    private String arenaName;
    private String discordName;
    private String realName;
    private String nickName;
    private String twitchName;
    private String twitterName;
    private String icon;
    private League league;

    public Player(final String arenaName, final String discordName, final String realName, final String nickName,
        final String twitchName, final String twitterName, final String icon, final League league) {
        this.arenaName = arenaName;
        this.discordName = discordName;
        this.realName = realName;
        this.nickName = nickName;
        this.twitchName = twitchName;
        this.twitterName = twitterName;
        this.icon = icon;
        this.league = league;
    }

    private Player(final String arenaName, final String discordName) {
        this(arenaName, discordName, null, null, null, null, null, League.NONE);
    }

    public String getName() {
        if (!Strings.isBlank(nickName)) {
            return nickName;
        }

        if (!Strings.isBlank(realName)) {
            return realName;
        }

        if (!Strings.isBlank(twitchName)) {
            return twitchName;
        }

        if (!Strings.isBlank(twitterName)) {
            return twitterName;
        }

        return getShortArenaName().replace('_', ' ');
    }

    public String getShortArenaName() {
        int hashTagIndex = arenaName.indexOf("#");
        if (hashTagIndex >= 0) {
            return arenaName.substring(0, hashTagIndex);
        }
        return arenaName;
    }

    public boolean isMpl() {
        return league == League.MPL;
    }

    public boolean isRivals() {
        return league == League.RIVALS;
    }

    // TODO: move this to mfo code
    public static Player createFromMfoName(final String name) {
        int commaIndex = name.indexOf(", ");
        if (commaIndex < 0) {
            return Player.BYE;
        }
        final String arenaName = name.substring(0, commaIndex);
        final String discordName = name.substring(commaIndex + 2);
        return new Player(arenaName,discordName);
    }

    public static Player createFromName(final String name) {
        return createFromName(name, null, League.NONE);
    }

    public static Player createFromName(final String name, final String icon) {
        return createFromName(name, icon, League.NONE);
    }

    public static Player createFromName(final String name, final League league) {
        return createFromName(name, null, league);
    }

    public static Player createFromName(final String name, final String icon, final League league) {
        return new Player(null, null, name, null, null, null, icon, league);
    }

    public static Player createFromSocials(final String name, final String twitchChannel, final String twitterName) {
        return new Player(null, null, name, null, twitchChannel, twitterName, null, League.NONE);
    }
}