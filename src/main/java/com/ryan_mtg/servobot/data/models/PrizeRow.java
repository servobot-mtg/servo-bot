package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.model.giveaway.Prize;
import com.ryan_mtg.servobot.utility.Validation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "prize")
public class PrizeRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "giveaway_id")
    private int giveawayId;

    private Prize.Status status;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String reward;

    @Column(name = "winner_id")
    private int winnerId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Prize.Status getStatus() {
        return status;
    }

    public void setStatus(final Prize.Status status) {
        this.status = status;
    }

    public void setGiveawayId(int giveawayId) {
        this.giveawayId = giveawayId;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }
}

