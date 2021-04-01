package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

public class EmoteLink {
    public static final int UNREGISTERED_ID = 0;

    @Getter @Setter
    private int id;

    @Getter
    private final String twitchEmote;

    @Getter
    private final String discordEmote;

    public EmoteLink(final int id, final String twitchEmote, final String discordEmote) throws UserError {
        this.id = id;
        this.twitchEmote = twitchEmote;
        this.discordEmote = discordEmote;

        Validation.validateStringLength(twitchEmote, Validation.MAX_EMOTE_LENGTH, "TwitchEmote");
        Validation.validateStringLength(discordEmote, Validation.MAX_EMOTE_LENGTH, "DiscordEmote");
    }
}
