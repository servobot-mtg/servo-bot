package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Player {
    public static final Player BYE = new Player("BYE", "BYE");

    private String arenaName;
    private String discordName;
    @Setter
    private String realName;
    @Setter
    private String nickName;
    @Setter
    private String twitchName;
    @Setter
    private String twitterName;

    public Player(final String arenaName, final String discordName, final String realName, final String nickName,
        final String twitchName, final String twitterName) {
        this.arenaName = arenaName;
        this.discordName = discordName;
        this.realName = realName;
        this.nickName = nickName;
        this.twitchName = twitchName;
        this.twitterName = twitterName;
    }

    private Player(final String arenaName, final String discordName) {
        this(arenaName, discordName, null, null, null, null);
    }

    public String getName() {
        if (!Strings.isBlank(nickName)) {
            return nickName;
        }

        if (!Strings.isBlank(twitchName)) {
            return twitchName;
        }

        if (!Strings.isBlank(twitterName)) {
            return twitterName;
        }

        if (!Strings.isBlank(realName)) {
            return realName;
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
}
