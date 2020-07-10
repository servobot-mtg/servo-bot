package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.TwitchChat;
import com.ryan_mtg.servobot.model.Channel;

public class TwitchChannel implements Channel {
    private TwitchChat twitchChat;
    private String channelName;

    public TwitchChannel(final TwitchClient twitchClient, final String channelName) {
        this.twitchChat = twitchClient.getChat();
        this.channelName = channelName;
    }

    @Override
    public void say(final String message) {
        if (!message.isEmpty()) {
            twitchChat.sendMessage(channelName, message);
        }
    }

    @Override
    public void sendImage(final String url, final String fileName, final String description) {
        throw new UnsupportedOperationException("Twitch doesn't support sending images");
    }
}
