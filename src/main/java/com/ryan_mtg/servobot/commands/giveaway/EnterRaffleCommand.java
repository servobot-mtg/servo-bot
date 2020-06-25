package com.ryan_mtg.servobot.commands.giveaway;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.giveaway.Raffle;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class EnterRaffleCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.ENTER_RAFFLE_COMMAND_TYPE;

    @Getter
    private int giveawayId;

    @Getter
    private String response;

    public EnterRaffleCommand(final int id, final CommandSettings commandSettings, final int giveawayId,
            final String response) throws UserError {
        super(id, commandSettings);
        this.giveawayId = giveawayId;
        this.response = response;

        Validation.validateStringLength(response, Validation.MAX_TEXT_LENGTH, "Response");
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        HomedUser entrant = event.getSender().getHomedUser();
        Raffle raffle = event.getHomeEditor().enterRaffle(entrant, giveawayId);

        if (entrant.isStreamer()) {
            event.say("%sender% has rigged the raffle!");
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
        commandVisitor.visitEnterGiveawayCommand(this);
    }
}
