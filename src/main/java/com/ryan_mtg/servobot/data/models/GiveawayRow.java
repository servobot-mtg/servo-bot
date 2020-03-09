package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "giveaway")
public class GiveawayRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    @Size(max = Validation.MAX_NAME_LENGTH)
    private String name;

    private int flags;

    private Giveaway.State state;

    @Column(name = "request_prize_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String requestPrizeCommandName;

    @Column(name = "prize_request_limit")
    private int prizeRequestLimit;

    @Column(name = "prize_request_user_limit")
    private int prizeRequestUserLimit;

    @Column(name = "request_prize_command_id")
    private int requestPrizeCommandId;

    @Column(name = "prize_requests")
    private int prizeRequests;

    @Column(name = "start_raffle_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    @Getter @Setter
    private String startRaffleCommandName;

    @Column(name = "start_raffle_flags")
    @Getter @Setter
    private int startRaffleFlags;

    @Column(name = "start_raffle_permission")
    @Getter @Setter
    private Permission startRafflePermission;

    @Column(name = "start_raffle_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    @Getter @Setter
    private String startRaffleMessage;

    @Column(name = "start_raffle_command_id")
    private int startRaffleCommandId;

    @Column(name = "enter_raffle_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    @Getter @Setter
    private String enterRaffleCommandName;

    @Column(name = "enter_raffle_flags")
    @Getter @Setter
    private int enterRaffleFlags;

    @Column(name = "enter_raffle_permission")
    @Getter @Setter
    private Permission enterRafflePermission;

    @Column(name = "enter_raffle_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    @Getter @Setter
    private String enterRaffleMessage;

    @Column(name = "raffle_status_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    @Getter @Setter
    private String raffleStatusCommandName;

    @Column(name = "raffle_status_flags")
    @Getter @Setter
    private int raffleStatusFlags;

    @Column(name = "raffle_status_permission")
    @Getter @Setter
    private Permission raffleStatusPermission;

    @Column(name = "raffle_status_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    @Getter @Setter
    private String raffleStatusMessage;


    @Column(name = "raffle_duration")
    private int raffleDuration;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getBotHomeId() {
        return botHomeId;
    }

    public void setBotHomeId(int botHomeId) {
        this.botHomeId = botHomeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public Giveaway.State getState() {
        return state;
    }

    public void setState(final Giveaway.State state) {
        this.state = state;
    }

    public String getRequestPrizeCommandName() {
        return requestPrizeCommandName;
    }

    public void setRequestPrizeCommandName(final String requestPrizeCommandName) {
        this.requestPrizeCommandName = requestPrizeCommandName;
    }

    public int getPrizeRequestLimit() {
        return prizeRequestLimit;
    }

    public void setPrizeRequestLimit(final int prizeRequestLimit) {
        this.prizeRequestLimit = prizeRequestLimit;
    }

    public int getPrizeRequestUserLimit() {
        return prizeRequestUserLimit;
    }

    public void setPrizeRequestUserLimit(final int prizeRequestUserLimit) {
        this.prizeRequestUserLimit = prizeRequestUserLimit;
    }

    public int getRequestPrizeCommandId() {
        return requestPrizeCommandId;
    }

    public void setRequestPrizeCommandId(final int requestPrizeCommandId) {
        this.requestPrizeCommandId = requestPrizeCommandId;
    }

    public int getPrizeRequests() {
        return prizeRequests;
    }

    public void setPrizeRequests(final int prizeRequests) {
        this.prizeRequests = prizeRequests;
    }

    public int getStartRaffleCommandId() {
        return startRaffleCommandId;
    }

    public void setStartRaffleCommandId(final int startRaffleCommandId) {
        this.startRaffleCommandId = startRaffleCommandId;
    }

    public int getRaffleDuration() {
        return raffleDuration;
    }

    public void setRaffleDuration(final int raffleDuration) {
        this.raffleDuration = raffleDuration;
    }
}
