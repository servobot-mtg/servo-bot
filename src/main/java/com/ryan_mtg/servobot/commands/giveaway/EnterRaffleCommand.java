package com.ryan_mtg.servobot.commands.giveaway;

import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.user.HomedUser;
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
        HomedUser entrant = event.getSender().getHomedUser();
        event.getHomeEditor().enterRaffle(entrant, giveawayId);

        if (entrant.isStreamer()) {
            MessageCommand.say(event, "%sender% has rigged the raffle!");
        } else {
            MessageCommand.say(event, response);
        }
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
