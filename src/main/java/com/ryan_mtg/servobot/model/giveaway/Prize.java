package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Validation;

public class Prize {
    public static final int UNREGISTERED_ID = 0;

    public enum Status {
        AVAILABLE,
        RESERVED,
        AWARDED,
        BESTOWED,
    }

    private int id;
    private Status status;
    private HomedUser winner;
    private String reward;

    public Prize(final int id, final String reward) throws BotErrorException {
        this.id = id;
        this.reward = reward;
        this.status = Status.AVAILABLE;

        Validation.validateStringLength(reward, Validation.MAX_TEXT_LENGTH, "Reward");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public HomedUser getWinner() {
        return winner;
    }

    public void setWinner(final HomedUser winner) {
        this.winner = winner;
    }

    public String getReward() {
        return reward;
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
