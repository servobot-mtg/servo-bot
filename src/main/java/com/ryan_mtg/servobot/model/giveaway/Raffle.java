package com.ryan_mtg.servobot.model.giveaway;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.EnterRaffleCommand;
import com.ryan_mtg.servobot.commands.RaffleStatusCommand;
import com.ryan_mtg.servobot.commands.SelectWinnerCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Entrant;
import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Raffle {
    public static final int UNREGISTERED_ID = 0;
    private static final Random RANDOM = new Random();

    public enum Status {
        IN_PROGRESS,
        CONCLUDED,
    }

    private int id;

    @Getter @Setter
    private Status status;

    private EnterRaffleCommand enterRaffleCommand;
    private RaffleStatusCommand raffleStatusCommand;
    private SelectWinnerCommand selectWinnerCommand;
    private List<Command> alertCommands;

    @Getter
    private List<Prize> prizes;
    private Instant stopTime;
    private List<Entrant> entrants = new ArrayList<>();

    public Raffle(final int id, final EnterRaffleCommand enterRaffleCommand,
                  final RaffleStatusCommand raffleStatusCommand, SelectWinnerCommand selectWinnerCommand,
                  final List<Command> alertCommands, final List<Prize> prizes, final Instant stopTime) {
        this.status = Status.IN_PROGRESS;
        this.enterRaffleCommand = enterRaffleCommand;
        this.raffleStatusCommand = raffleStatusCommand;
        this.selectWinnerCommand = selectWinnerCommand;
        this.alertCommands = alertCommands;
        this.prizes = prizes;
        this.stopTime = stopTime;
    }

    public Duration getTimeLeft() {
        if (stopTime == null) {
            return null;
        }

        Duration result = Duration.between(Instant.now(), stopTime);
        if (result.compareTo(Duration.ofSeconds(0)) < 0) {
            return Duration.ofSeconds(0);
        }
        return result;
    }

    public int getEntrantCount() {
        return entrants.size();
    }

    public List<HomedUser> selectWinners(final Giveaway giveaway, final CommandTable commandTable,
                                  final GiveawayEdit giveawayEdit) throws BotErrorException {
        if (!getTimeLeft().isZero()) {
            throw new BotErrorException("Selecting, but the raffle isn't over!");
        }

        setStatus(Raffle.Status.CONCLUDED);

        List<HomedUser> winners = new ArrayList<>();

        for (Prize prize : prizes) {
            if (entrants.size() > 0) {
                Entrant entrant = entrants.get(RANDOM.nextInt(entrants.size()));
                prize.awardTo(entrant.getUser());
                winners.add(entrant.getUser());
                entrants.remove(entrant);
            } else {
                prize.setStatus(Prize.Status.AVAILABLE);
            }
            giveawayEdit.addPrize(giveaway.getId(), prize);
        }

        giveawayEdit.merge(commandTable.deleteCommand(enterRaffleCommand.getId()));
        if (raffleStatusCommand != null) {
            giveawayEdit.merge(commandTable.deleteCommand(raffleStatusCommand.getId()));
        }
        if (selectWinnerCommand != null) {
            giveawayEdit.merge(commandTable.deleteCommand(selectWinnerCommand.getId()));
        }
        for (Command alertCommand : alertCommands) {
            giveawayEdit.merge(commandTable.deleteCommand(alertCommand.getId()));
        }
        return winners;
    }

    public void enter(final HomedUser user) throws BotErrorException {
        if (status != Status.IN_PROGRESS) {
            throw new BotErrorException("Cannot enter raffle, it is no longer in progress.");
        }
        for (Entrant entrant : entrants) {
            if (entrant.getUser().getId() == user.getId()) {
                throw new BotErrorException(String.format("%s has already entered the raffle.", user.getName()));
            }
        }
        Entrant entrant = new Entrant(user);
        entrants.add(entrant);
    }
}
