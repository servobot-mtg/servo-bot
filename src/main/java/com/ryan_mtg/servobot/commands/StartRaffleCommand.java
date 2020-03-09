package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.Raffle;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

import java.time.temporal.ChronoUnit;

public class StartRaffleCommand extends MessageCommand {
    public static final int TYPE = 20;
    private int giveawayId;

    @Getter
    private String message;

    public StartRaffleCommand(final int id, final int flags, final Permission permission, final int giveawayId,
                              final String message) throws BotErrorException {
        super(id, flags, permission);
        this.giveawayId = giveawayId;
        this.message = message;

        Validation.validateStringLength(message, Validation.MAX_TEXT_LENGTH, "Start raffle message");
    }

    public int getGiveawayId() {
        return giveawayId;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        HomeEditor homeEditor = event.getHomeEditor();
        Raffle raffle = homeEditor.startRaffle(giveawayId);
        Giveaway giveaway = homeEditor.getGiveaway(giveawayId);

        long minutesLeft = raffle.getTimeLeft().plus(30, ChronoUnit.SECONDS).toMinutes();
        String message =
                String.format("A raffle has started, sponsored by Zlubar Gaming! It will last %d minutes. To enter type !%s #sponsored",
                        minutesLeft, giveaway.getRaffleSettings().getEnterRaffle().getCommandName());
        MessageCommand.say(event, message);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitStartGiveawayCommand(this);
    }
}
