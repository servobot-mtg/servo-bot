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
    private String enterRaffleCommandName =  "enter";
    private String raffleStatusCommandName =  "status";
    private Duration raffleDuration = Duration.of(10, ChronoUnit.MINUTES);

    public RaffleSettings() {
        startRaffle = new CommandSettings("giveaway", DEFAULT_FLAGS, Permission.STREAMER,
                "The raffle is starting.");
    }

    public RaffleSettings(final CommandSettings startRaffle, final String enterRaffleCommandName,
                          final String raffleStatusCommandName, final Duration raffleDuration) {
        this.startRaffle = startRaffle;
        this.enterRaffleCommandName = enterRaffleCommandName;
        this.raffleStatusCommandName = raffleStatusCommandName;
        this.raffleDuration = raffleDuration;
    }

    public String getEnterRaffleCommandName() {
        return enterRaffleCommandName;
    }

    public boolean hasRaffleStatusCommand() {
        return raffleStatusCommandName != null && !raffleStatusCommandName.isEmpty();
    }

    public String getRaffleStatusCommandName() {
        return raffleStatusCommandName;
    }

    public Duration getRaffleDuration() {
        return raffleDuration;
    }

    public void validate(final RaffleSettings previousSettings, final CommandTable commandTable)
            throws BotErrorException  {
        Validation.validateCommandSettings(startRaffle, previousSettings.getStartRaffle(), commandTable, true,
                START_RAFFLE_DESCRIPTION);

        Validation.validateSetTemporaryCommandName(enterRaffleCommandName, previousSettings.getEnterRaffleCommandName(),
                commandTable, true, ENTER_RAFFLE_DESCRIPTION);

        Validation.validateSetTemporaryCommandName(raffleStatusCommandName,
                previousSettings.getRaffleStatusCommandName(), commandTable, false, RAFFLE_STATUS_DESCRIPTION);

        Validation.validateNotSame(startRaffle.getCommandName(), enterRaffleCommandName, START_RAFFLE_DESCRIPTION,
                ENTER_RAFFLE_DESCRIPTION);
        Validation.validateNotSame(startRaffle.getCommandName(), raffleStatusCommandName, START_RAFFLE_DESCRIPTION,
                RAFFLE_STATUS_DESCRIPTION);
        Validation.validateNotSame(enterRaffleCommandName, raffleStatusCommandName, ENTER_RAFFLE_DESCRIPTION,
                RAFFLE_STATUS_DESCRIPTION);
    }

    public void validate(final CommandTable commandTable) throws BotErrorException {
        if (enterRaffleCommandName == null || enterRaffleCommandName.isEmpty()) {
            throw new BotErrorException("No enter raffle command is set");
        }
        if (commandTable.getCommand(enterRaffleCommandName) != null) {
            throw new BotErrorException(String.format("There is already a '%s' command.", enterRaffleCommandName));
        }

        if (raffleStatusCommandName != null && !raffleStatusCommandName.isEmpty()) {
            if (commandTable.getCommand(raffleStatusCommandName) != null) {
                throw new BotErrorException(String.format("There is already a '%s' command.", raffleStatusCommandName));
            }
        }
    }
}

