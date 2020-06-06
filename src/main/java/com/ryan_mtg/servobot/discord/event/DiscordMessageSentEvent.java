package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.discord.model.DiscordChannel;
import com.ryan_mtg.servobot.discord.model.DiscordHome;
import com.ryan_mtg.servobot.discord.model.DiscordMessage;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.MessageHomeEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiscordMessageSentEvent extends DiscordBotHomeEvent implements MessageHomeEvent {
    private GuildMessageReceivedEvent event;
    private User sender;

    public DiscordMessageSentEvent(final GuildMessageReceivedEvent event, final BotHome botHome, final User sender) {
        super(botHome);
        this.event = event;
        this.sender = sender;
    }

    @Override
    public DiscordHome getHome() {
        return new DiscordHome(event.getGuild(), getHomeEditor());
    }

    @Override
    public Channel getChannel() {
        return new DiscordChannel(getHome(), event.getChannel());
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
}
