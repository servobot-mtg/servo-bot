package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;

public class EnterGiveawayCommand extends MessageCommand {
    public static final int TYPE = 21;

    public EnterGiveawayCommand(final int id, final int flags, final Permission permission) {
        super(id, flags, permission);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        if (event.getMessage().getServiceType() != DiscordService.TYPE) {
            throw new BotErrorException("Enter the giveaway in the discord ().");
        }
        event.getHomeEditor().enterGiveaway(event.getSender().getHomedUser());
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
