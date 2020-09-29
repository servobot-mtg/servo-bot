package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.TwitchChat;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.utility.Strings;

import java.util.Map;
import java.util.regex.Pattern;

public class TwitchChannel implements Channel {
    private static final Pattern NAME_PATTERN = Pattern.compile("@[a-z_A-Z][a-z_A-Z0-9]*");
    private static final Pattern EMOTE_PATTERN =
            Pattern.compile("(\\b[a-z_A-Z][a-z_A-Z0-9]*\\b)|(:[a-z_A-Z][a-z_A-Z0-9]*:)");

    private final TwitchChat twitchChat;
    private final TwitchServiceHome serviceHome;
    private final String channelName;

    public TwitchChannel(final TwitchClient twitchClient, final TwitchServiceHome serviceHome,
            final String channelName) {
        this.twitchChat = twitchClient.getChat();
        this.serviceHome = serviceHome;
        this.channelName = channelName;
    }

    @Override
    public void say(final String message) {
        if (!message.isEmpty()) {
            twitchChat.sendMessage(channelName, replaceEmotes(message));
        }
    }

    @Override
    public Message sayAndWait(final String text) {
        twitchChat.sendMessage(channelName, replaceEmotes(text));
        return null;
    }

    @Override
    public void sendImage(final String url, final String fileName, final String description) {
        throw new UnsupportedOperationException("Twitch doesn't support sending images");
    }

    private String replaceEmotes(final String text) {
        Map<String, Emote> emoteMap = serviceHome.getEmoteMap();
        return Strings.replace(text, EMOTE_PATTERN, matcher -> {
            String name = matcher.group();
            if (name.startsWith(":")) {
                name = name.substring(1, name.length() - 1);
            }
            if (emoteMap.containsKey(name)) {
                return emoteMap.get(name).getMessageText();
            }
            return null;
        });
    }
}