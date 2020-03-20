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
@Getter @Setter
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
    private String startRaffleCommandName;

    @Column(name = "start_raffle_flags")
    private int startRaffleFlags;

    @Column(name = "start_raffle_permission")
    private Permission startRafflePermission;

    @Column(name = "start_raffle_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String startRaffleMessage;

    @Column(name = "start_raffle_command_id")
    private int startRaffleCommandId;

    @Column(name = "enter_raffle_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String enterRaffleCommandName;

    @Column(name = "enter_raffle_flags")
    private int enterRaffleFlags;

    @Column(name = "enter_raffle_permission")
    private Permission enterRafflePermission;

    @Column(name = "enter_raffle_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String enterRaffleMessage;

    @Column(name = "raffle_status_command_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String raffleStatusCommandName;

    @Column(name = "raffle_status_flags")
    private int raffleStatusFlags;

    @Column(name = "raffle_status_permission")
    private Permission raffleStatusPermission;

    @Column(name = "raffle_status_message")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String raffleStatusMessage;

    @Column(name = "raffle_duration")
    private int raffleDuration;

    @Column(name = "raffle_winner_count")
    private int raffleWinnerCount;

    @Column(name = "raffle_winner_response")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String raffleWinnerResponse;

    @Column(name = "discord_channel")
    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String discordChannel;
}
