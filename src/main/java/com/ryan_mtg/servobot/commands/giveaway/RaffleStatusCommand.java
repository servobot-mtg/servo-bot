package com.ryan_mtg.servobot.commands.giveaway;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.Raffle;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class RaffleStatusCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.RAFFLE_STATUS_COMMAND_TYPE;

    @Getter
    private int giveawayId;

    @Getter
    private final String response;

    public RaffleStatusCommand(final int id, final CommandSettings commandSettings, final int giveawayId,
            final String response) throws UserError {
        super(id, commandSettings);
        this.giveawayId = giveawayId;
        this.response = response;

        Validation.validateStringLength(response, Validation.MAX_TEXT_LENGTH, "Response");
    }

    public void setGiveawayId(final int giveawayId) {
        this.giveawayId = giveawayId;
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        Giveaway giveaway = event.getHomeEditor().getGiveaway(giveawayId);
        Raffle raffle = giveaway.retrieveCurrentRaffle();
        if (raffle == null) {
            event.say("There is no raffle currently running.");
        } else {
            SimpleSymbolTable symbolTable = new SimpleSymbolTable();
            symbolTable.addValue("raffle", raffle);
            event.say(symbolTable, response);
        }
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitGiveawayStatusCommand(this);
    }
}
