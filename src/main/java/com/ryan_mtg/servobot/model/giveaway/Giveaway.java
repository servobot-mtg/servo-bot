package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.RequestPrizeCommand;
import com.ryan_mtg.servobot.commands.StartRaffleCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Validation;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Giveaway {
    public static final int UNREGISTERED_ID = 0;

    private static final String DEFAULT_PRIZE_REQUEST_COMMAND = "code";

    public enum State {
        CONFIGURING,
        ACTIVE,
        COMPLETE,
    }

    private int id;
    private String name;
    private boolean selfService;
    private boolean rafflesEnabled;
    private State state;

    // Self Service
    private String requestPrizeCommandName;
    private RequestPrizeCommand requestPrizeCommand;
    private int prizeRequestLimit = 50;
    private int prizeRequestUserLimit = 1;
    private int prizeRequests = 0;

    // Raffle
    private String startRaffleCommandName;
    private StartRaffleCommand startRaffleCommand;
    private String enterRaffleCommandName;
    private String raffleStatusCommandName;
    private Duration raffleDuration = Duration.of(10, ChronoUnit.MINUTES);

    private List<Prize> prizes = new ArrayList<>();
    private List<Raffle> raffles = new ArrayList<>();

    public Giveaway(final int id, final String name, final boolean selfService, final boolean rafflesEnabled) throws BotErrorException {
        this.id = id;
        this.name = name;
        this.selfService = selfService;
        this.rafflesEnabled = rafflesEnabled;
        this.state = State.CONFIGURING;

        Validation.validateStringValue(name, Validation.MAX_NAME_LENGTH, "Giveaway name",
                Validation.NAME_PATTERN);

        if (!selfService && !rafflesEnabled) {
            throw new BotErrorException("Giveaway must be at least one of self service or raffle.");
        }

        if (selfService) {
            requestPrizeCommandName = DEFAULT_PRIZE_REQUEST_COMMAND;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public boolean isSelfService() {
        return selfService;
    }

    public boolean isRafflesEnabled() {
        return rafflesEnabled;
    }

    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public String getRequestPrizeCommandName() {
        return requestPrizeCommandName;
    }

    public void setRequestPrizeCommandName(final String requestPrizeCommandName) throws BotErrorException {
        Validation.validateStringValue(requestPrizeCommandName, Validation.MAX_NAME_LENGTH,
                "Request prize command name", Validation.NAME_PATTERN);

        this.requestPrizeCommandName = requestPrizeCommandName;
    }

    public Command getRequestPrizeCommand() {
        return requestPrizeCommand;
    }

    public void setRequestPrizeCommand(final RequestPrizeCommand requestPrizeCommand) {
        this.requestPrizeCommand = requestPrizeCommand;
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

    public String getStartRaffleCommandName() {
        return startRaffleCommandName;
    }

    public void setStartRaffleCommandName(final String startRaffleCommandName) throws BotErrorException {
        Validation.validateStringValue(startRaffleCommandName, Validation.MAX_NAME_LENGTH,
                "Start raffle command name", Validation.NAME_PATTERN);

        this.startRaffleCommandName = startRaffleCommandName;
    }

    public Command getStartRaffleCommand() {
        return startRaffleCommand;
    }

    public void setStartRaffleCommand(final StartRaffleCommand startRaffleCommand) {
        this.startRaffleCommand = startRaffleCommand;
    }

    public Duration getRaffleDuration() {
        return raffleDuration;
    }

    public void setRaffleDuration(final Duration raffleDuration) {
        this.raffleDuration = raffleDuration;
    }

    public String getEnterRaffleCommandName() {
        return enterRaffleCommandName;
    }

    public void setEnterRaffleCommandName(final String enterRaffleCommandName) throws BotErrorException {
        Validation.validateStringValue(enterRaffleCommandName, Validation.MAX_NAME_LENGTH,
                "Enter raffle command name", Validation.NAME_PATTERN);

        this.enterRaffleCommandName = enterRaffleCommandName;
    }

    public String getRaffleStatusCommandName() {
        return raffleStatusCommandName;
    }

    public void setRaffleStatusCommandName(final String raffleStatusCommandName) throws BotErrorException {
        Validation.validateStringValue(raffleStatusCommandName, Validation.MAX_NAME_LENGTH,
                "Raffle status command name", Validation.NAME_PATTERN);

        this.raffleStatusCommandName = raffleStatusCommandName;
    }

    public List<Prize> getPrizes() {
        return prizes;
    }

    public void addPrize(final Prize prize) {
        prizes.add(prize);
    }

    public List<Raffle> getRaffles() {
        return raffles;
    }

    public void addRaffle(final Raffle raffle) {
        raffles.add(raffle);
    }

    public Raffle retrieveCurrentRaffle() throws BotErrorException {
        for(Raffle raffle : raffles) {
            if (raffle.getStatus() == Raffle.Status.IN_PROGRESS) {
                return raffle;
            }
        }
        throw new BotErrorException("There is no raffle currently in progress.");
    }


    /*
    public List<Reward> getRewards() {
        return rewards;
    }

    public Reward getCurrentReward() {
        return currentReward;
    }

    public void addReward(final Reward reward) {
        rewards.add(reward);
    }
    */

    public GiveawayEdit start(final CommandTable commandTable) throws BotErrorException {
        GiveawayEdit giveawayEdit = new GiveawayEdit();
        switch (state) {
            case ACTIVE:
                return giveawayEdit;
            case COMPLETE:
                throw new BotErrorException("Can't start a giveaway that has been completed.");
        }

        if (selfService) {
            if (requestPrizeCommandName == null || requestPrizeCommandName.isEmpty()) {
                throw new BotErrorException("Must set request prize command name");
            }

            if (commandTable.getCommand(requestPrizeCommandName) != null) {
                throw new BotErrorException(String.format("There is already a '%s' command.", requestPrizeCommandName));
            }

            int flags = Command.DEFAULT_FLAGS | Command.TEMPORARY_FLAG;
            requestPrizeCommand =
                    new RequestPrizeCommand(Command.UNREGISTERED_ID, flags, Permission.ANYONE, id);

            CommandTableEdit commandTableEdit = commandTable.addCommand(requestPrizeCommandName, requestPrizeCommand);
            giveawayEdit.merge(commandTableEdit);
        }
        giveawayEdit.addGiveaway(this);
        return giveawayEdit;
    }

    public GiveawayEdit requestPrize(final HomedUser requester) throws BotErrorException {
        if (!selfService) {
            throw new BotErrorException("Cannot request a prize from this type of giveaway");
        }

        GiveawayEdit giveawayEdit = new GiveawayEdit();

        Prize prize = prizes.stream().filter(p -> p.getStatus() == Prize.Status.AVAILABLE).findFirst()
                .orElseThrow(() -> new BotErrorException("No prizes left :("));

        prize.bestowTo(requester);
        giveawayEdit.addPrize(getId(), prize);
        return giveawayEdit;
    }

    public GiveawayEdit reservePrize() throws BotErrorException {
        if (!rafflesEnabled) {
            throw new BotErrorException("Cannot reserve a prize from this type of giveaway");
        }

        GiveawayEdit giveawayEdit = new GiveawayEdit();

        Prize prize = prizes.stream().filter(p -> p.getStatus() == Prize.Status.AVAILABLE).findFirst()
                .orElseThrow(() -> new BotErrorException("No prizes left :("));

        prize.setStatus(Prize.Status.RESERVED);
        giveawayEdit.addPrize(getId(), prize);
        return giveawayEdit;
    }

    public GiveawayEdit bestowPrize(final int prizeId) throws BotErrorException {
        Prize prize = getPrize(prizeId);

        if (prize.getStatus() != Prize.Status.AWARDED) {
            throw new BotErrorException(String.format("Invalid prize state for bestowing: %s", prize.getStatus()));
        }

        prize.setStatus(Prize.Status.BESTOWED);
        GiveawayEdit giveawayEdit = new GiveawayEdit();
        giveawayEdit.addPrize(getId(), prize);
        return giveawayEdit;
    }

    private Prize getPrize(final int prizeId) throws BotErrorException {
        for (Prize prize : prizes) {
            if (prize.getId() == prizeId) {
                return prize;
            }
        }
        throw new BotErrorException(String.format("No prize with id %d", prizeId));
    }
}
