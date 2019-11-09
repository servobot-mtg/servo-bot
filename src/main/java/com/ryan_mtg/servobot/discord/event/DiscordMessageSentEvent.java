package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.discord.model.DiscordMessage;
import com.ryan_mtg.servobot.discord.model.DiscordUser;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiscordMessageSentEvent extends DiscordEvent implements MessageSentEvent {
    private GuildMessageReceivedEvent event;

    public DiscordMessageSentEvent(final GuildMessageReceivedEvent event, final int homeId) {
        super(homeId);
        this.event = event;
    }

    @Override
    public Message getMessage() {
        return new DiscordMessage(event.getMessage());
    }

    @Override
    public User getSender() {
        return new DiscordUser(event.getMember());
    }
}
