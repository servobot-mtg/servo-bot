package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Strings;
import net.dv8tion.jda.api.JDA;
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
    public String getBotName() {
        return guild.getSelfMember().getEffectiveName();
    }

    @Override
    public ServiceHome getServiceHome(int serviceType) {
        return null;
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
    public boolean isStreaming() {
        Member owner = guild.getOwner();
        return owner.getActivities().stream()
                .anyMatch(activity -> activity.getType() == Activity.ActivityType.STREAMING);
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
    public boolean hasRole(final User user, final String roleName) {
        String deampedRoleName = deamp(roleName);
        Member member = getMember(user);
        return member.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase(deampedRoleName));
    }

    @Override
    public boolean hasRole(final String roleName) {
        return !guild.getRolesByName(deamp(roleName), false).isEmpty();
    }

    @Override
    public void clearRole(final User user, final String roleName) throws UserError {
        Member member = getMember(user);
        guild.removeRoleFromMember(member, getRole(roleName)).queue();
    }

    @Override
    public void setRole(final User user, final String roleName) throws UserError {
        guild.addRoleToMember(getMember(user), getRole(roleName)).queue();
    }

    @Override
    public List<String> clearRole(final String roleName) throws UserError {
        Role role = getRole(roleName);
        List<Member> members = guild.getMembersWithRoles(role);
        List<String> names = new ArrayList<>();
        for (Member member : members) {
            guild.removeRoleFromMember(member, role).queue();
            names.add(member.getEffectiveName());
        }
        return names;
    }

    @Override
    public boolean isHigherRanked(final User firstUser, final User secondUser) {
        Member firstMember = getMember(firstUser);
        Member secondMember = getMember(secondUser);
        return getPosition(firstMember) > getPosition(secondMember);
    }

    @Override
    public boolean hasUser(final String userName) {
        if (Strings.isBlank(userName)) {
            return false;
        }

        return !guild.getMembersByEffectiveName(deamp(userName), true).isEmpty();
    }

    @Override
    public User getUser(final String userName) throws UserError {
        Member member = getMember(userName);
        HomedUser homedUser = getHomeEditor().getUserByDiscordId(member.getIdLong(), member.getEffectiveName());
        return new DiscordUser(homedUser , member);
    }

    @Override
    public Emote getEmote(final String emoteName) {
        List<net.dv8tion.jda.api.entities.Emote> emotes = guild.getEmotesByName(emoteName, true);
        if (!emotes.isEmpty()) {
            net.dv8tion.jda.api.entities.Emote emote = emotes.get(0);
            return new DiscordEmote(emote);
        }

        JDA jda = guild.getJDA();

        emotes = jda.getEmotesByName(emoteName, true);
        if (!emotes.isEmpty()) {
            net.dv8tion.jda.api.entities.Emote emote = emotes.get(0);
            return new DiscordEmote(emote);
        }

        LOGGER.warn("Unable to find emote " + emoteName + " in " + guild);
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

    private Member getMember(final String username) throws UserError {
        if (username == null) {
            throw new UserError("No one specified.");
        }

        List<Member> members = guild.getMembersByEffectiveName(deamp(username), true);
        if (members.isEmpty()) {
            throw new UserError(String.format("No user named '%s'.", deamp(username)));
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

    private Role getRole(final String roleName) throws UserError {
        List<Role> roles = guild.getRolesByName(deamp(roleName), false);
        if (roles.isEmpty()) {
            throw new UserError(String.format("'%s' is not a valid role.", roleName));
        }
        return roles.get(0);
    }

    private Member getMember(final User user) {
        return ((DiscordUser)user).getMember();
    }

    private String deamp(final String string) {
        if (string.startsWith("@")) {
            return string.substring(1);
        }
        return string;
    }
}
