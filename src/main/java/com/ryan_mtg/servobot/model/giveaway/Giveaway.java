package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.giveaway.RequestPrizeCommand;
import com.ryan_mtg.servobot.commands.giveaway.StartRaffleCommand;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.HomedUserTable;
import com.ryan_mtg.servobot.utility.Validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private RaffleSettings raffleSettings;
    private StartRaffleCommand startRaffleCommand;

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

        if (rafflesEnabled) {
            raffleSettings = new RaffleSettings();
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

    public RaffleSettings getRaffleSettings() {
        return raffleSettings;
    }

    public void setRaffleSettings(final RaffleSettings raffleSettings) {
        this.raffleSettings = raffleSettings;
    }

    public Command getStartRaffleCommand() {
        return startRaffleCommand;
    }

    public void setStartRaffleCommand(final StartRaffleCommand startRaffleCommand) {
        this.startRaffleCommand = startRaffleCommand;
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

    public GiveawayEdit start(final int botHomeId, final CommandTable commandTable) throws BotErrorException {
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
            requestPrizeCommand = new RequestPrizeCommand(Command.UNREGISTERED_ID,
                new CommandSettings(flags, Permission.ANYONE, new RateLimit()), id);

            CommandTableEdit commandTableEdit = commandTable.addCommand(requestPrizeCommandName, requestPrizeCommand);
            giveawayEdit.merge(commandTableEdit);
        }
        giveawayEdit.addGiveaway(botHomeId, this);
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
        giveawayEdit.savePrize(getId(), prize);
        return giveawayEdit;
    }

    public GiveawayEdit reservePrizes(final int winnerCount) throws BotErrorException {
        if (!rafflesEnabled) {
            throw new BotErrorException("Cannot reserve a prize from this type of giveaway");
        }

        GiveawayEdit giveawayEdit = new GiveawayEdit();

        List<Prize> reservedPrizes = prizes.stream().filter(p -> p.getStatus() == Prize.Status.AVAILABLE)
                .limit(winnerCount).collect(Collectors.toList());

        if (reservedPrizes.isEmpty()) {
            throw new BotErrorException("No prizes left");
        }

        for (Prize prize : reservedPrizes) {
            prize.setStatus(Prize.Status.RESERVED);
            giveawayEdit.savePrize(getId(), prize);
        }
        return giveawayEdit;
    }

    public GiveawayEdit bestowPrize(final int prizeId) throws BotErrorException {
        Prize prize = getPrize(prizeId);

        if (prize.getStatus() != Prize.Status.AWARDED) {
            throw new BotErrorException(String.format("Invalid prize state for bestowing: %s", prize.getStatus()));
        }

        prize.setStatus(Prize.Status.BESTOWED);
        GiveawayEdit giveawayEdit = new GiveawayEdit();
        giveawayEdit.savePrize(getId(), prize);
        return giveawayEdit;
    }

    public GiveawayEdit deletePrize(final int prizeId) throws BotErrorException {
        Prize prize = getPrize(prizeId);

        GiveawayEdit giveawayEdit = new GiveawayEdit();

        if (prize.getStatus() == Prize.Status.RESERVED) {
            throw new BotErrorException("Cannot delete a prize that is reserved");
        }

        prizes.remove(prize);

        giveawayEdit.deletePrize(prize);
        return giveawayEdit;
    }

    public GiveawayEdit mergeUser(final HomedUserTable homedUserTable, final int newUserId,
            final List<Integer> oldUserIds) throws BotErrorException {
        GiveawayEdit giveawayEdit = new GiveawayEdit();
        for (Prize prize : prizes) {
            if (prize.getWinner() != null && oldUserIds.contains(prize.getWinner().getId())) {
                prize.setWinner(homedUserTable.getById(newUserId));
                giveawayEdit.savePrize(id, prize);
            }
        }
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