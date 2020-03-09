package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

public class Prize {
    public static final int UNREGISTERED_ID = 0;

    public enum Status {
        AVAILABLE,
        RESERVED,
        AWARDED,
        BESTOWED,
    }

    @Getter @Setter
    private int id;

    @Getter @Setter
    private Status status;

    @Getter @Setter
    private HomedUser winner;

    @Getter
    private String reward;

    @Getter
    private String description;

    public Prize(final int id, final String reward, final String description) throws BotErrorException {
        this.id = id;
        this.status = Status.AVAILABLE;
        this.reward = reward;
        this.description = description;

        Validation.validateStringLength(reward, Validation.MAX_TEXT_LENGTH, "Reward");
        Validation.validateStringLength(description, Validation.MAX_TEXT_LENGTH, "Description");
    }

    public void awardTo(final HomedUser winner) {
        setStatus(Status.AWARDED);
        this.winner = winner;
    }

    public void bestowTo(final HomedUser winner) {
        setStatus(Status.BESTOWED);
        this.winner = winner;
    }
}
