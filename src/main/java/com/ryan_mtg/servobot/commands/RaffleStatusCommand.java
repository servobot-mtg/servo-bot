package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.Raffle;
import com.ryan_mtg.servobot.model.scope.FunctorSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class RaffleStatusCommand extends MessageCommand {
    public static final int TYPE = 22;
    @Getter
    private int giveawayId;

    @Getter
    private String response;

    public RaffleStatusCommand(final int id, final int flags, final Permission permission, final int giveawayId,
                               final String response) throws BotErrorException {
        super(id, flags, permission);
        this.giveawayId = giveawayId;
        this.response = response;

        Validation.validateStringLength(response, Validation.MAX_TEXT_LENGTH, "Response");
    }

    public void setGiveawayId(final int giveawayId) {
        this.giveawayId = giveawayId;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Giveaway giveaway = event.getHomeEditor().getGiveaway(giveawayId);
        Raffle raffle = giveaway.retrieveCurrentRaffle();
        if (raffle == null) {
            MessageCommand.say(event, "There is no raffle currently running.");
        } else {
            FunctorSymbolTable symbolTable = new FunctorSymbolTable();
            symbolTable.addValue("raffle", raffle);
            MessageCommand.say(event, symbolTable, response);
        }
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitGiveawayStatusCommand(this);
    }
}
