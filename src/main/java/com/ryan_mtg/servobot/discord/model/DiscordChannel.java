package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.utility.Strings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DiscordChannel implements Channel {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordChannel.class);
    private static final Pattern NAME_PATTERN = Pattern.compile("@[a-z_A-Z][a-z_A-Z0-9-]*");
    private static final Pattern EMOTE_PATTERN =
            Pattern.compile("(\\b[a-z_A-Z][a-z_A-Z0-9]*\\b)|(:[a-z_A-Z][a-z_A-Z0-9]*:)");
    private MessageChannel channel;

    private DiscordServiceHome serviceHome;

    public DiscordChannel(final DiscordServiceHome serviceHome, final MessageChannel channel) {
        this.channel = channel;
        this.serviceHome = serviceHome;
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
        Guild guild = serviceHome.getGuild();
        final String nameReplacedText = Strings.replace(text, NAME_PATTERN, matcher -> {
            String name = matcher.group().substring(1);
            List<Member> members = guild.getMembersByName(name, true);
            if (members.isEmpty()) {
                return null;
            }
            return members.get(0).getAsMention();
        });

        Map<String, Emote> emoteMap = serviceHome.getEmoteMap();
        return Strings.replace(nameReplacedText, EMOTE_PATTERN, matcher -> {
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
