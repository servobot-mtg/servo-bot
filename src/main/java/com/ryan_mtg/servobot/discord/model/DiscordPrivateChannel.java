package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.user.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordPrivateChannel implements Channel {
    private final DiscordService discordService;
    private final MessageChannel channel;
    private final User user;

    public DiscordPrivateChannel(final DiscordService discordService, final User user, final MessageChannel channel) {
        this.discordService = discordService;
        this.user = user;
        this.channel = channel;
    }

    @Override
    public void say(final String message) {
        if (!message.isEmpty()) {
            discordService.logSendMessage(user, message);
            channel.sendMessage(message).queue();
        }
    }
}
