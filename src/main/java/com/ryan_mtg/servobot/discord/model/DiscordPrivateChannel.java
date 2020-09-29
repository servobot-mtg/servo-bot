package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.user.User;
import net.dv8tion.jda.api.entities.MessageChannel;

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

    @Override
    public Message sayAndWait(final String text) throws UserError {
        discordService.logSendMessage(user, text);
        return new DiscordMessage(discordService.getBotUser(), channel.sendMessage(text).complete());
    }

    @Override
    public void sendImage(final String url, final String fileName, final String description) {
        throw new UnsupportedOperationException("Sending images on private channels not supported yet");
    }
}