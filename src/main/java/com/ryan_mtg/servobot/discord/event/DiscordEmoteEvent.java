package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.discord.model.DiscordChannel;
import com.ryan_mtg.servobot.discord.model.DiscordSavedMessage;
import com.ryan_mtg.servobot.events.EmoteHomeEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.scope.Scope;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;

public class DiscordEmoteEvent extends DiscordBotHomeEvent implements EmoteHomeEvent {
    private final GenericGuildMessageReactionEvent event;
    private final User reactor;
    private final Emote emote;

    public DiscordEmoteEvent(final GenericGuildMessageReactionEvent event, final BotHome botHome,
            final User reactor, final Emote emote) {
        super(botHome);
        this.event = event;
        this.reactor = reactor;
        this.emote = emote;
    }

    @Override
    public Scope getScope() {
        return null;
    }

    @Override
    public Channel getChannel() {
        return new DiscordChannel(getServiceHome(), event.getChannel());
    }

    @Override
    public User getSender() {
        return reactor;
    }

    @Override
    public Message getMessage() {
        return new DiscordSavedMessage(getServiceHome(), event.getChannel().getIdLong(), event.getMessageIdLong());
    }

    @Override
    public Emote getEmote() {
        return emote;
    }
}
