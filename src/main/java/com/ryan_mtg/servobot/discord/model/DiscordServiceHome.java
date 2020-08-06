package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiscordServiceHome implements ServiceHome {
    private static Logger LOGGER = LoggerFactory.getLogger(DiscordServiceHome.class);

    private final DiscordService discordService;

    @Getter
    private final long guildId;

    @Getter
    private Guild guild;

    @Getter @Setter
    private HomeEditor homeEditor;

    private List<Emote> cachedEmotes;

    public DiscordServiceHome(final DiscordService discordService, final long guildId) {
        this.discordService = discordService;
        this.guildId = guildId;
    }

    @Override
    public Service getService() {
        return discordService;
    }

    @Override
    public int getServiceType() {
        return DiscordService.TYPE;
    }

    @Override
    public String getName() {
        if (guild != null) {
            return guild.getName();
        }
        return "???";
    }

    @Override
    public String getBotName() {
        return guild.getSelfMember().getNickname();
    }

    @Override
    public String getLink() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return guild.getIconUrl();
    }

    @Override
    public String getDescription() {
        return String.format("Guild %s", getName());
    }

    @Override
    public boolean isStreaming() {
        return discordService.isStreaming(guildId);
    }

    @Override
    public void setStatus(String status) {

    }

    @Override
    public void setName(final String botName) {
        Member self = guild.getSelfMember();
        if (!botName.equals(self.getNickname())) {
            guild.modifyNickname(guild.getSelfMember(), botName).queue();
        }
    }

    @Override
    public boolean isStreamer(final User user) {
        return guild.getOwner().getIdLong() == getDiscordId(user);
    }

    @Override
    public void start(final BotHome botHome) {
        guild = discordService.getGuild(guildId);
        setName(botHome.getBotName());
    }

    @Override
    public void stop(BotHome botHome) {
        guild = null;
    }

    @Override
    public List<String> getChannels() {
        if (guild != null) {
            return guild.getTextChannels().stream().map(GuildChannel::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Channel getChannel(final String channelName) throws UserError {
        List<TextChannel> channels = guild.getTextChannelsByName(channelName, true);
        if (channels.isEmpty()) {
            throw new UserError("No channel with name %s.", channelName);
        }
        return new DiscordChannel(this, channels.get(0));
    }

    @Override
    public List<String> getRoles() {
        if (guild != null) {
            return guild.getRoles().stream().filter(role -> !role.isManaged() && !role.isPublicRole())
                    .map(Role::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public String getRole(final User user) {
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
    public boolean isHigherRanked(final User user, final User otherUser) throws UserError {
        Member firstMember = getMember(user);
        Member secondMember = getMember(otherUser);
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
        return new DiscordUser(homedUser, member);
    }

    @Override
    public User getUser(final HomedUser homedUser) {
        Member member = guild.getMemberById(homedUser.getDiscordId());
        return new DiscordUser(homedUser, member);
    }

    @Override
    public Map<String, Emote> getEmoteMap() {
        return homeEditor.getEmoteMap(DiscordService.TYPE);
    }

    @Override
    public Emote getEmote(final String emoteName) {
        List<net.dv8tion.jda.api.entities.Emote> emotes = guild.getEmotesByName(emoteName, true);
        if (!emotes.isEmpty() && emotes.get(0).canInteract(guild.getSelfMember())) {
            net.dv8tion.jda.api.entities.Emote emote = emotes.get(0);
            return new DiscordEmote(emote);
        }

        JDA jda = guild.getJDA();

        emotes = jda.getEmotesByName(emoteName, true);
        if (!emotes.isEmpty() && emotes.get(0).canInteract(guild.getSelfMember())) {
            net.dv8tion.jda.api.entities.Emote emote = emotes.get(0);
            return new DiscordEmote(emote);
        }

        LOGGER.warn("Unable to find emote {} in {}", emoteName, guild);
        return null;
    }

    @Override
    public List<Emote> getEmotes() {
        if (cachedEmotes != null) {
            return cachedEmotes;
        }
        updateEmotes();
        return cachedEmotes;
    }

    public void updateEmotes() {
        if (guild != null) {
            List<net.dv8tion.jda.api.entities.Emote> emotes = guild.getEmotes();
            cachedEmotes = emotes.stream().map(emote -> new DiscordEmote(emote)).collect(Collectors.toList());
        } else {
            cachedEmotes = new ArrayList<>();
        }
    }

    private long getDiscordId(final User user) {
        return ((DiscordUser) user).getDiscordId();
    }

    private Member getMember(final User user) {
        return ((DiscordUser)user).getMember();
    }

    private Member getMember(final String username) throws UserError {
        if (Strings.isBlank(username)) {
            throw new UserError("No one specified.");
        }

        List<Member> members = guild.getMembersByEffectiveName(deamp(username), true);
        if (members.isEmpty()) {
            throw new UserError(String.format("No user named '%s'.", deamp(username)));
        }
        return members.get(0);
    }

    private String deamp(final String string) {
        if (string.startsWith("@")) {
            return string.substring(1);
        }
        return string;
    }

    private Role getRole(final String roleName) throws UserError {
        List<Role> roles = guild.getRolesByName(deamp(roleName), false);
        if (roles.isEmpty()) {
            throw new UserError(String.format("'%s' is not a valid role.", roleName));
        }
        return roles.get(0);
    }

    private int getPosition(final Member member) {
        int position = -1;
        for (Role role : member.getRoles()) {
            position = Math.max(position, role.getPosition());
        }
        return position;
    }
}
