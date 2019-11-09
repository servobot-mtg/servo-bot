package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Home;
import net.dv8tion.jda.api.entities.MessageChannel;

public class DiscordChannel implements Channel {
    private MessageChannel channel;
    private Home home;

    public DiscordChannel(final Home home, final MessageChannel channel) {
        this.channel = channel;
        this.home = home;
    }

    @Override
    public Home getHome() {
        return home;
    }

    @Override
    public void say(final String message) {
        channel.sendMessage(message).queue();
    }
}
