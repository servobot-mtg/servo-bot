package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DiscordHome implements Home {
    private static Logger LOGGER = LoggerFactory.getLogger(DiscordHome.class);

    private Guild guild;
    private HomeEditor homeEditor;

    public DiscordHome(final Guild guild, final HomeEditor homeEditor) {
        this.guild = guild;
        this.homeEditor = homeEditor;
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
        List<TextChannel> channels = guild.getTextChannelsByName(channelName, true);
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
        if (!roles.isEmpty()) {
            return roles.get(0).getName();
        }
        return "Pleb";
    }

    @Override
    public void setRole(final User user, final String roleName) throws BotErrorException {
        long discordId = user.getHomedUser().getDiscordId();
        if (discordId == 0) {
            throw new BotErrorException("User is not registered on Discord.");
        }
        Member member = guild.getMemberById(discordId);
        List<Role> roles = guild.getRolesByName(roleName, false);
        if (roles.isEmpty()) {
            throw new BotErrorException(String.format("'%s' is not a valid role.", roleName));
        }
        guild.addRoleToMember(member, roles.get(0)).queue();
    }

    @Override
    public void setRole(final String username, final String roleName) throws BotErrorException {
        Member member = getMember(username);
        List<Role> roles = guild.getRolesByName(roleName, false);
        if (roles.isEmpty()) {
            throw new BotErrorException(String.format("'%s' is not a valid role.", roleName));
        }
        guild.addRoleToMember(member, roles.get(0)).queue();
    }

    @Override
    public List<String> clearRole(final String roleName) throws BotErrorException {
        List<Role> roles = guild.getRolesByName(roleName, false);
        if (roles.isEmpty()) {
            throw new BotErrorException(String.format("'%s' is not a valid role.", roleName));
        }

        Role role = roles.get(0);
        List<Member> members = guild.getMembersWithRoles(role);
        List<String> names = new ArrayList<>();
        for (Member member : members) {
            guild.removeRoleFromMember(member, role).queue();
            names.add(member.getEffectiveName());
        }
        return names;
    }

    @Override
    public boolean isHigherRanked(final String firstUserName, final User secondUser) throws BotErrorException {
        Member firstMember = getMember(firstUserName);
        Member secondMember = ((DiscordUser)secondUser).getMember();
        return getPosition(firstMember) > getPosition(secondMember);
    }

    @Override
    public Emote getEmote(final String emoteName) {
        List<net.dv8tion.jda.api.entities.Emote> emotes = guild.getEmotesByName(emoteName, true);
        if (!emotes.isEmpty()) {
            net.dv8tion.jda.api.entities.Emote emote = emotes.get(0);
            return new DiscordEmote(emote);
        }

        JDA jda = guild.getJDA();

        emotes = jda.getEmotes();
        for (net.dv8tion.jda.api.entities.Emote e : emotes) {
            LOGGER.info(e.getName());
        }

        emotes = jda.getEmotesByName(emoteName, true);
        if (!emotes.isEmpty()) {
            net.dv8tion.jda.api.entities.Emote emote = emotes.get(0);
            return new DiscordEmote(emote);
        }

        LOGGER.warn("Unable to find emote " + emoteName + " in " + guild);
        emotes = guild.getEmotes();
        for (net.dv8tion.jda.api.entities.Emote emote : emotes) {
            LOGGER.info("  " + emote.getName());
        }

        return null;
    }

    @Override
    public User getUser(final HomedUser homedUser) {
        Member member = guild.getMemberById(homedUser.getDiscordId());
        return new DiscordUser(homedUser, member);
    }

    @Override
    public HomeEditor getHomeEditor() {
        return homeEditor;
    }

    @Override
    public void setStatus(final String status) {
        guild.getJDA().getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, status));
    }

    private long getDiscordId(final User user) {
        return ((DiscordUser) user).getDiscordId();
    }

    public Guild getGuild() {
        return guild;
    }

    private Member getMember(final String username) throws BotErrorException {
        List<Member> members = guild.getMembersByEffectiveName(username, true);
        if (members.isEmpty()) {
            throw new BotErrorException(String.format("No user named '%s'.", username));
        }
        return members.get(0);
    }

    private int getPosition(final Member member) {
        int position = -1;
        for (Role role : member.getRoles()) {
            if (!role.getName().toLowerCase().contains("jail")) {
                position = Math.max(position, role.getPosition());
            }
        }
        return position;
    }
}
