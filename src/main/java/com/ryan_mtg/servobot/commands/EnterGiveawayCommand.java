package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;

public class EnterGiveawayCommand extends MessageCommand {
    public static final int TYPE = 21;
    private int giveawayId;

    public EnterGiveawayCommand(final int id, final int flags, final Permission permission, final int giveawayId) {
        super(id, flags, permission);
        this.giveawayId = giveawayId;
    }

    public int getGiveawayId() {
        return giveawayId;
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        event.getHomeEditor().enterGiveaway(event.getSender().getHomedUser(), giveawayId);
        MessageCommand.say(event, String.format("%s has been entered.", event.getSender().getName()));
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
