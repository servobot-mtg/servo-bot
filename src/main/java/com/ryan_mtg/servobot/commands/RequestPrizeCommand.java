package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.Prize;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.user.HomedUser;

public class RequestPrizeCommand extends MessageCommand {
    public static final int TYPE = 28;
    private int giveawayId;

    public RequestPrizeCommand(final int id, final int flags, final Permission permission, final int giveawayId) {
        super(id, flags, permission);
        this.giveawayId = giveawayId;
    }

    public int getGiveawayId() {
        return giveawayId;
    }

    @Override
    public void perform(final MessageSentEvent messageSentEvent, final String arguments) throws BotErrorException {
        HomeEditor homeEditor = messageSentEvent.getHomeEditor();
        User sender = messageSentEvent.getSender();
        HomedUser homedSender = sender.getHomedUser();
        Giveaway giveaway = homeEditor.getGiveaway(giveawayId);

        String userRequestVariableName = String.format("#redemption_%d", giveawayId);
        IntegerStorageValue userRequestCount =
                homeEditor.incrementStorageValue(homedSender.getId(), userRequestVariableName, 0);
        if (userRequestCount.getValue() > giveaway.getPrizeRequestUserLimit()) {
            throw new BotErrorException(String.format("%s has sent too many requests!", sender.getName()));
        }

        String requestVariableName = String.format("#total_redemptions_%d", giveawayId);
        IntegerStorageValue requestCount =
                homeEditor.incrementStorageValue(homedSender.getId(), requestVariableName, 0);
        if (requestCount.getValue() > giveaway.getPrizeRequestLimit()) {
            throw new BotErrorException(String.format("Sorry %s, the prize barrel is empty.", sender.getName()));
        }

        Prize prize = homeEditor.requestPrize(giveawayId, homedSender);
        String message = String.format("Congratulations %s, your code is: %s", sender.getName(), prize.getReward());
        sender.whisper(message);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitRequestPrizeCommand(this);
    }
}
