package com.ryan_mtg.servobot.commands.giveaway;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.Prize;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;

public class RequestPrizeCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.REQUEST_PRIZE_COMMAND_TYPE;

    @Getter
    private int giveawayId;

    public RequestPrizeCommand(final int id, final CommandSettings commandSettings, final int giveawayId) {
        super(id, commandSettings);
        this.giveawayId = giveawayId;
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        HomeEditor homeEditor = event.getHomeEditor();
        User sender = event.getSender();
        HomedUser homedSender = sender.getHomedUser();
        Giveaway giveaway = homeEditor.getGiveaway(giveawayId);

        String userRequestVariableName = String.format("#redemption_%d", giveawayId);
        IntegerStorageValue userRequestCount =
                homeEditor.incrementStorageValue(homedSender.getId(), userRequestVariableName, 0);
        if (userRequestCount.getValue() > giveaway.getPrizeRequestUserLimit()) {
            throw new UserError("%s has sent too many requests!", sender.getName());
        }

        String requestVariableName = String.format("#total_redemptions_%d", giveawayId);
        IntegerStorageValue requestCount =
                homeEditor.incrementStorageValue(homedSender.getId(), requestVariableName, 0);
        if (requestCount.getValue() > giveaway.getPrizeRequestLimit()) {
            throw new UserError("Sorry %s, the prize barrel is empty.", sender.getName());
        }

        Prize prize = homeEditor.requestPrize(giveawayId, homedSender);
        String message = String.format("Congratulations %s, your code is: %s", sender.getName(), prize.getReward());
        event.getServiceHome(event.getServiceType()).getService().whisper(sender.getUser(), message);
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitRequestPrizeCommand(this);
    }
}
