package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.utility.Flags;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class RaffleSettings {
    private static final String START_RAFFLE_DESCRIPTION = "Start raffle command name";
    private static final String ENTER_RAFFLE_DESCRIPTION = "Enter raffle command name";
    private static final String RAFFLE_STATUS_DESCRIPTION = "Raffle status command name";
    private static final String SELECT_RAFFLE_WINNER_DESCRIPTION = "Select raffle winner command name";
    private static final int DEFAULT_COMMAND_FLAGS = Command.TEMPORARY_FLAG | Command.TWITCH_FLAG;
    private static final int TIMED_FLAG = 1;

    @Getter
    private GiveawayCommandSettings startRaffle;

    @Getter
    private GiveawayCommandSettings enterRaffle;

    @Getter
    private GiveawayCommandSettings raffleStatus;

    @Getter
    private GiveawayCommandSettings selectWinner;

    @Getter
    private Duration duration = Duration.of(10, ChronoUnit.MINUTES);

    @Getter
    private int winnerCount = 1;

    @Getter
    private int flags;

    @Getter
    private String discordChannel;

    public RaffleSettings() {
        startRaffle = new GiveawayCommandSettings("giveaway", DEFAULT_COMMAND_FLAGS, Permission.STREAMER,
    "A raffle has started! It will last %raffle.timeLeft%. To enter type !%raffle.enterCommandName%");
        enterRaffle = new GiveawayCommandSettings("enter", DEFAULT_COMMAND_FLAGS, Permission.ANYONE,
    "%sender% has been entered.");
        raffleStatus = new GiveawayCommandSettings("status", DEFAULT_COMMAND_FLAGS, Permission.ANYONE,
    "There are %raffle.timeLeft% minutes left in the raffle. Type !%raffle.enterRaffleCommandName% to enter.");
        selectWinner = new GiveawayCommandSettings("award", DEFAULT_COMMAND_FLAGS, Permission.STREAMER,
                "The raffle winner is %winner%.");
    }

    public RaffleSettings(final int flags, final GiveawayCommandSettings startRaffle,
            final GiveawayCommandSettings enterRaffle, final GiveawayCommandSettings raffleStatus,
            final Duration duration, final int winnerCount, final GiveawayCommandSettings selectWinner,
            final String discordChannel) {
        this.flags = flags;
        this.startRaffle = startRaffle;
        this.enterRaffle = enterRaffle;
        this.raffleStatus = raffleStatus;
        this.duration = duration;
        this.winnerCount = winnerCount;
        this.selectWinner = selectWinner;
        this.discordChannel = discordChannel;
    }

    public RaffleSettings(final boolean timed, final GiveawayCommandSettings startRaffle,
                          final GiveawayCommandSettings enterRaffle, final GiveawayCommandSettings raffleStatus,
                          final Duration duration, final int winnerCount, final GiveawayCommandSettings selectWinner,
                          final String discordChannel) {
        this(timed ? TIMED_FLAG : 0, startRaffle, enterRaffle, raffleStatus, duration, winnerCount, selectWinner,
                discordChannel);
    }

    public boolean isTimed() {
        return Flags.hasFlag(flags, TIMED_FLAG);
    }

    public boolean hasRaffleStatusCommand() {
        return raffleStatus.getCommandName() != null && !raffleStatus.getCommandName().isEmpty();
    }

    public void validateOnSave(final RaffleSettings previousSettings, final CommandTable commandTable)
            throws UserError {
        Validation.validateCommandSettings(startRaffle, previousSettings.getStartRaffle(), commandTable, true,
                START_RAFFLE_DESCRIPTION);
        Validation.validateCommandSettings(enterRaffle, previousSettings.getEnterRaffle(), commandTable, true,
                ENTER_RAFFLE_DESCRIPTION);
        Validation.validateCommandSettings(raffleStatus, previousSettings.getRaffleStatus(), commandTable, false,
                RAFFLE_STATUS_DESCRIPTION);
        Validation.validateCommandSettings(selectWinner, previousSettings.getSelectWinner(), commandTable, !isTimed(),
                SELECT_RAFFLE_WINNER_DESCRIPTION);

        Validation.validateNotSame(startRaffle.getCommandName(), enterRaffle.getCommandName(), START_RAFFLE_DESCRIPTION,
                ENTER_RAFFLE_DESCRIPTION);
        Validation.validateNotSame(startRaffle.getCommandName(), raffleStatus.getCommandName(), START_RAFFLE_DESCRIPTION,
                RAFFLE_STATUS_DESCRIPTION);
        Validation.validateNotSame(enterRaffle.getCommandName(), raffleStatus.getCommandName(), ENTER_RAFFLE_DESCRIPTION,
                RAFFLE_STATUS_DESCRIPTION);
        if (!Strings.isBlank(selectWinner.getCommandName())) {
            Validation.validateNotSame(selectWinner.getCommandName(), startRaffle.getCommandName(),
                    SELECT_RAFFLE_WINNER_DESCRIPTION, START_RAFFLE_DESCRIPTION);
            Validation.validateNotSame(selectWinner.getCommandName(), enterRaffle.getCommandName(),
                    SELECT_RAFFLE_WINNER_DESCRIPTION, ENTER_RAFFLE_DESCRIPTION);
            Validation.validateNotSame(selectWinner.getCommandName(), raffleStatus.getCommandName(),
                    SELECT_RAFFLE_WINNER_DESCRIPTION, RAFFLE_STATUS_DESCRIPTION);
        }

        Validation.validateStringLength(discordChannel, Validation.MAX_TEXT_LENGTH, "Discord channel");
    }

    public void validateOnStart(final CommandTable commandTable) throws UserError {
        if (enterRaffle.getCommandName() == null || enterRaffle.getCommandName().isEmpty()) {
            throw new UserError("No enter raffle command is set");
        }
        if (commandTable.getCommand(getEnterRaffle().getCommandName()) != null) {
            throw new UserError("There is already a '%s' command.", enterRaffle.getCommandName());
        }

        if (hasRaffleStatusCommand()) {
            if (commandTable.getCommand(raffleStatus.getCommandName()) != null) {
                throw new UserError("There is already a '%s' command.", raffleStatus.getCommandName());
            }
        }
    }
}

