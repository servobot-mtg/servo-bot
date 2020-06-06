package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.discord.model.DiscordMessage;
import com.ryan_mtg.servobot.discord.model.DiscordPrivateChannel;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.MessageEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.scope.Scope;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class DiscordPrivateMessageEvent extends DiscordEvent implements MessageEvent {
    private final DiscordService discordService;
    private final PrivateMessageReceivedEvent event;
    private final User sender;

    public DiscordPrivateMessageEvent(final DiscordService discordService, final PrivateMessageReceivedEvent event,
            final User sender) {
        this.discordService = discordService;
        this.event = event;
        this.sender = sender;
    }

    @Override
    public Channel getChannel() {
        return new DiscordPrivateChannel(discordService, sender.getUser(), event.getChannel());
    }

    @Override
    public Message getMessage() {
        return new DiscordMessage(this, event.getMessage());
    }

    @Override
    public User getSender() {
        return sender;
    }

    @Override
    public int getServiceType() {
        return DiscordService.TYPE;
    }

    @Override
    public Scope getScope() {
        return getBotEditor().getScope();
    }
}
