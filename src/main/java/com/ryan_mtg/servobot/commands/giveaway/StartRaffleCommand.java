package com.ryan_mtg.servobot.commands.giveaway;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.giveaway.Raffle;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class StartRaffleCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.START_RAFFLE_COMMAND_TYPE;

    @Getter
    private int giveawayId;

    @Getter
    private String message;

    public StartRaffleCommand(final int id, final CommandSettings commandSettings, final int giveawayId,
                              final String message) throws BotErrorException {
        super(id, commandSettings);
        this.giveawayId = giveawayId;
        this.message = message;

        Validation.validateStringLength(message, Validation.MAX_TEXT_LENGTH, "Start raffle message");
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        HomeEditor homeEditor = event.getHomeEditor();
        Raffle raffle = homeEditor.startRaffle(giveawayId);

        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        symbolTable.addValue("raffle", raffle);
        MessageCommand.say(event, symbolTable, message);
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitStartGiveawayCommand(this);
    }
}
