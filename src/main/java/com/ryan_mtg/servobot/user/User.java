package com.ryan_mtg.servobot.user;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.utility.Flags;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

public class User {
    public static final int UNREGISTERED_ID = 0;

    private static final int ADMIN_FLAG = 1;
    private static final int INVITE_FLAG = 1<<1;

    @Getter
    private int id;

    @Getter
    private int flags;

    @Getter
    private int twitchId;

    @Getter
    private long discordId;

    @Getter @Setter
    private String twitchUsername;

    @Getter @Setter
    private String discordUsername;

    @Getter @Setter
    private String arenaUsername;

    public User(final int id, final int flags, final int twitchId, final String twitchUsername, final long discordId,
            final String discordUsername, final String arenaUsername) throws BotErrorException {
        this.id = id;
        this.flags = flags;
        this.twitchId = twitchId;
        this.twitchUsername = twitchUsername;
        this.discordId = discordId;
        this.discordUsername = discordUsername;
        this.arenaUsername = arenaUsername;

        Validation.validateStringLength(twitchUsername, Validation.MAX_USERNAME_LENGTH, "Twitch username");
        Validation.validateStringLength(discordUsername, Validation.MAX_USERNAME_LENGTH, "Discord username");
        Validation.validateStringLength(arenaUsername, Validation.MAX_USERNAME_LENGTH, "Arena username");
    }

    public boolean isAdmin() {
        return Flags.hasFlag(flags, ADMIN_FLAG);
    }

    public String getName() {
        if (twitchUsername != null) {
            return twitchUsername;
        }
        return discordUsername;
    }

    public boolean hasInvite() {
        return Flags.hasFlag(flags, INVITE_FLAG);
    }

    public void invite() {
        setFlag(INVITE_FLAG, true);

    }

    public void removeInvite() {
        setFlag(INVITE_FLAG, false);
    }

    private void setFlag(final int flag, final boolean value) {
        flags = Flags.setFlag(flags, flag, value);
    }
}
