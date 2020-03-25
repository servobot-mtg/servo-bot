package com.ryan_mtg.servobot.commands.giveaway;

import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.giveaway.Raffle;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class StartRaffleCommand extends MessageCommand {
    public static final int TYPE = 20;

    @Getter
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

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        HomeEditor homeEditor = event.getHomeEditor();
        Raffle raffle = homeEditor.startRaffle(giveawayId);

        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        symbolTable.addValue("raffle", raffle);
        MessageCommand.say(event, symbolTable, message);
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
