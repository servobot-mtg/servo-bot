package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordChannel implements Channel {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordChannel.class);
    private static final Pattern NAME_PATTERN = Pattern.compile("@[a-z_A-Z][a-z_A-Z0-9]*");
    private static final Pattern EMOTE_PATTERN =
            Pattern.compile("(\\b[a-z_A-Z][a-z_A-Z0-9]*\\b)|(:[a-z_A-Z][a-z_A-Z0-9]*:)");
    private MessageChannel channel;

    private DiscordHome home;

    public DiscordChannel(final DiscordHome home, final MessageChannel channel) {
        this.channel = channel;
        this.home = home;
    }

    @Override
    public void say(final String message) {
        if (!message.isEmpty()) {
            channel.sendMessage(replaceNames(message)).queue();
        }
    }

    @Override
    public void sendImage(final String url, final String fileName, final String description) throws UserError {
        try {
            InputStream file = new URL(url).openStream();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setImage("attachment://" + fileName).setDescription(description);
            channel.sendFile(file, fileName).embed(embed.build()).queue();
        } catch (IOException e) {
            throw new UserError(e, "Unable to download %s", fileName);
        }
    }

    private String replaceNames(final String text) {
        //TODO: Fix this once channels are constructed appropriately
        if (home == null) {
            return text;
        }
        Guild guild = home.getGuild();
        final String nameReplacedText = replace(text, NAME_PATTERN, matcher -> {
            String name = matcher.group().substring(1);
            List<Member> members = guild.getMembersByName(name, true);
            if (members.isEmpty()) {
                return null;
            }
            return members.get(0).getAsMention();
        });

        List<Emote> emotes = home.getEmotes();
        Map<String, Emote> emoteMap = new HashMap<>();
        emotes.forEach(emote -> emoteMap.put(emote.getName(), emote));
        return replace(nameReplacedText, EMOTE_PATTERN, matcher -> {
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

    private String replace(final String text, final Pattern pattern, final Function<Matcher, String> replaceFunction) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder message = new StringBuilder();
        int index = 0;
        while(matcher.find(index)) {
            int start = matcher.start();
            int end = matcher.end();

            String replacement = replaceFunction.apply(matcher);
            if (replacement == null) {
                message.append(text, index, end);
            } else {
                message.append(text, index, start);
                message.append(replacement);
            }

            index = end;
        }

        message.append(text.substring(index));
        return message.toString();
    }
}
