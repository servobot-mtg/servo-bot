package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class EnterRaffleCommand extends MessageCommand {
    public static final int TYPE = 21;

    @Getter
    private int giveawayId;
    @Getter
    private String response;

    public EnterRaffleCommand(final int id, final int flags, final Permission permission, final int giveawayId,
                              final String response) throws BotErrorException {
        super(id, flags, permission);
        this.giveawayId = giveawayId;
        this.response = response;

        Validation.validateStringLength(response, Validation.MAX_TEXT_LENGTH, "Response");
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        event.getHomeEditor().enterRaffle(event.getSender().getHomedUser(), giveawayId);
        MessageCommand.say(event, response);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitEnterGiveawayCommand(this);
    }
}
