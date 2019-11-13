package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DiscordHome implements Home {
    private static Logger LOGGER = LoggerFactory.getLogger(DiscordHome.class);

    private Guild guild;

    public DiscordHome(final Guild guild) {
        this.guild = guild;
    }

    @Override
    public String getName() {
        return guild.getName();
    }

    @Override
    public Channel getChannel(final String channelName, final int serviceType) {
        if (serviceType != DiscordService.TYPE) {
            return null;
        }
        List<TextChannel> channels = guild.getTextChannelsByName(channelName, false);
        if (channels.size() > 0) {
            return new DiscordChannel(this, channels.get(0));
        }
        throw new IllegalArgumentException(channelName + " is not a channel in " + guild);
    }

    @Override
    public boolean isStreamer(final User user) {
        return guild.getOwner().getIdLong() == getDiscordId(user);
    }

    @Override
    public String getRole(final User user, final int serviceType) {
        if (serviceType != DiscordService.TYPE) {
            return "Wanderer";
        }
        Member member = guild.getMemberById(getDiscordId(user));
        List<Role> roles = member.getRoles();
        for (Role role : roles) {
            return role.getName();
        }
        return "Pleb";
    }

    @Override
    public Emote getEmote(final String emoteName) {
        List<net.dv8tion.jda.api.entities.Emote> emotes = guild.getEmotesByName(emoteName, true);
        if (!emotes.isEmpty()) {
            net.dv8tion.jda.api.entities.Emote emote = emotes.get(0);
            return new DiscordEmote(emote);
        }

        LOGGER.warn("Unable to find emote " + emoteName + " in " + guild);
        return null;
    }

    private long getDiscordId(final User user) {
        return ((DiscordUser) user).getDiscordId();
    }
}
