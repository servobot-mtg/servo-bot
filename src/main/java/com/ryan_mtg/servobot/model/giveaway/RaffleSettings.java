package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class RaffleSettings {
    private static final String START_RAFFLE_DESCRIPTION = "Start raffle command name";
    private static final String ENTER_RAFFLE_DESCRIPTION = "Enter raffle command name";
    private static final String RAFFLE_STATUS_DESCRIPTION = "Raffle status command name";

    private static final int DEFAULT_FLAGS = Command.TEMPORARY_FLAG | Command.TWITCH_FLAG;

    @Getter
    private CommandSettings startRaffle;

    @Getter
    private CommandSettings enterRaffle;

    @Getter
    private CommandSettings raffleStatus;
    private Duration raffleDuration = Duration.of(10, ChronoUnit.MINUTES);

    public RaffleSettings() {
        startRaffle = new CommandSettings("giveaway", DEFAULT_FLAGS, Permission.STREAMER,
                "The raffle is starting.");
        startRaffle = new CommandSettings("enter", DEFAULT_FLAGS, Permission.ANYONE,
                "%sender% has been entered.");
        raffleStatus = new CommandSettings("status", DEFAULT_FLAGS, Permission.ANYONE,
                "There are %raffle.timeLeft% minutes left in the raffle. Type !%raffle.enterCommandName%");
    }

    public RaffleSettings(final CommandSettings startRaffle, final CommandSettings enterRaffle,
                          final CommandSettings raffleStatus, final Duration raffleDuration) {
        this.startRaffle = startRaffle;
        this.enterRaffle = enterRaffle;
        this.raffleStatus = raffleStatus;
        this.raffleDuration = raffleDuration;
    }

    public boolean hasRaffleStatusCommand() {
        return raffleStatus.getCommandName() != null && !raffleStatus.getCommandName().isEmpty();
    }

    public Duration getRaffleDuration() {
        return raffleDuration;
    }

    public void validate(final RaffleSettings previousSettings, final CommandTable commandTable)
            throws BotErrorException  {
        Validation.validateCommandSettings(startRaffle, previousSettings.getStartRaffle(), commandTable, true,
                START_RAFFLE_DESCRIPTION);

        Validation.validateCommandSettings(enterRaffle, previousSettings.getEnterRaffle(), commandTable, true,
                ENTER_RAFFLE_DESCRIPTION);

        Validation.validateCommandSettings(raffleStatus, previousSettings.getRaffleStatus(), commandTable, false,
                ENTER_RAFFLE_DESCRIPTION);

        Validation.validateNotSame(startRaffle.getCommandName(), enterRaffle.getCommandName(), START_RAFFLE_DESCRIPTION,
                ENTER_RAFFLE_DESCRIPTION);
        Validation.validateNotSame(startRaffle.getCommandName(), raffleStatus.getCommandName(), START_RAFFLE_DESCRIPTION,
                RAFFLE_STATUS_DESCRIPTION);
        Validation.validateNotSame(enterRaffle.getCommandName(), raffleStatus.getCommandName(), ENTER_RAFFLE_DESCRIPTION,
                RAFFLE_STATUS_DESCRIPTION);
    }

    public void validate(final CommandTable commandTable) throws BotErrorException {
        if (enterRaffle.getCommandName() == null || enterRaffle.getCommandName().isEmpty()) {
            throw new BotErrorException("No enter raffle command is set");
        }
        if (commandTable.getCommand(getEnterRaffle().getCommandName()) != null) {
            throw new BotErrorException(
                    String.format("There is already a '%s' command.", enterRaffle.getCommandName()));
        }

        if (hasRaffleStatusCommand()) {
            if (commandTable.getCommand(raffleStatus.getCommandName()) != null) {
                throw new BotErrorException(
                        String.format("There is already a '%s' command.", raffleStatus.getCommandName()));
            }
        }
    }
}

