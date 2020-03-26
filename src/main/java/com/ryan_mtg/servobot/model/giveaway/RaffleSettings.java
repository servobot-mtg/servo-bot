package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
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
    private GiveawayCommandSettings startRaffle;

    @Getter
    private GiveawayCommandSettings enterRaffle;

    @Getter
    private GiveawayCommandSettings raffleStatus;

    @Getter
    private Duration duration = Duration.of(10, ChronoUnit.MINUTES);

    @Getter
    private int winnerCount = 1;

    @Getter
    private String winnerResponse;

    @Getter
    private String discordChannel;

    public RaffleSettings() {
        startRaffle = new GiveawayCommandSettings("giveaway", DEFAULT_FLAGS, Permission.STREAMER,
    "A raffle has started! It will last %raffle.timeLeft%. To enter type !%raffle.enterCommandName%");
        enterRaffle = new GiveawayCommandSettings("enter", DEFAULT_FLAGS, Permission.ANYONE,
    "%sender% has been entered.");
        raffleStatus = new GiveawayCommandSettings("status", DEFAULT_FLAGS, Permission.ANYONE,
    "There are %raffle.timeLeft% minutes left in the raffle. Type !%raffle.enterRaffleCommandName% to enter.");

        winnerResponse = "The raffle winner is %winner%.";
    }

    public RaffleSettings(final GiveawayCommandSettings startRaffle, final GiveawayCommandSettings enterRaffle,
                          final GiveawayCommandSettings raffleStatus, final Duration duration, final int winnerCount,
                          final String winnerResponse, final String discordChannel) {
        this.startRaffle = startRaffle;
        this.enterRaffle = enterRaffle;
        this.raffleStatus = raffleStatus;
        this.duration = duration;
        this.winnerCount = winnerCount;
        this.winnerResponse = winnerResponse;
        this.discordChannel = discordChannel;
    }

    public boolean hasRaffleStatusCommand() {
        return raffleStatus.getCommandName() != null && !raffleStatus.getCommandName().isEmpty();
    }

    public void validateOnSave(final RaffleSettings previousSettings, final CommandTable commandTable)
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

        Validation.validateStringLength(winnerResponse, Validation.MAX_TEXT_LENGTH, "Winner response");
        Validation.validateStringLength(discordChannel, Validation.MAX_TEXT_LENGTH, "Discord channel");
    }

    public void validateOnStart(final CommandTable commandTable) throws BotErrorException {
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

