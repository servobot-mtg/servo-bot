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
    private String raffleStatusCommandName =  "status";
    private Duration raffleDuration = Duration.of(10, ChronoUnit.MINUTES);

    public RaffleSettings() {
        startRaffle = new CommandSettings("giveaway", DEFAULT_FLAGS, Permission.STREAMER,
                "The raffle is starting.");
        startRaffle = new CommandSettings("enter", DEFAULT_FLAGS, Permission.ANYONE,
                "%sender% has been entered.");
    }

    public RaffleSettings(final CommandSettings startRaffle, final CommandSettings enterRaffle,
                          final String raffleStatusCommandName, final Duration raffleDuration) {
        this.startRaffle = startRaffle;
        this.enterRaffle = enterRaffle;
        this.raffleStatusCommandName = raffleStatusCommandName;
        this.raffleDuration = raffleDuration;
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

        Validation.validateCommandSettings(enterRaffle, previousSettings.getEnterRaffle(), commandTable, true,
                ENTER_RAFFLE_DESCRIPTION);

        Validation.validateSetTemporaryCommandName(raffleStatusCommandName,
                previousSettings.getRaffleStatusCommandName(), commandTable, false, RAFFLE_STATUS_DESCRIPTION);

        Validation.validateNotSame(startRaffle.getCommandName(), enterRaffle.getCommandName(), START_RAFFLE_DESCRIPTION,
                ENTER_RAFFLE_DESCRIPTION);
        Validation.validateNotSame(startRaffle.getCommandName(), raffleStatusCommandName, START_RAFFLE_DESCRIPTION,
                RAFFLE_STATUS_DESCRIPTION);
        Validation.validateNotSame(enterRaffle.getCommandName(), raffleStatusCommandName, ENTER_RAFFLE_DESCRIPTION,
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

        if (raffleStatusCommandName != null && !raffleStatusCommandName.isEmpty()) {
            if (commandTable.getCommand(raffleStatusCommandName) != null) {
                throw new BotErrorException(String.format("There is already a '%s' command.", raffleStatusCommandName));
            }
        }
    }
}

