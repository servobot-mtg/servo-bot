package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordChannel implements Channel {
    private static final Pattern NAME_PATTERN = Pattern.compile("@[a-z_A-Z][a-z_A-Z0-9]*");
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

    private String replaceNames(final String text) {
        Guild guild = home.getGuild();
        StringBuilder message = new StringBuilder();

        Matcher matcher = NAME_PATTERN.matcher(text);
        int index = 0;

        while(matcher.find(index)) {
            int start = matcher.start();
            int end = matcher.end();
            String name = text.substring(start + 1, end);
            List<Member> members = guild.getMembersByName(name, true);
            if (!members.isEmpty()) {
                message.append(text, index, start);
                message.append(members.get(0).getAsMention());
            } else {
                message.append(text, index, end);
            }
            index = end;
        }

        message.append(text.substring(index));
        return message.toString();
    }
}
