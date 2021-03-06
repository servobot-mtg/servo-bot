package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.Role;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiscordServiceHome implements ServiceHome {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordServiceHome.class);

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
    public List<Channel> getChannels() {
        if (guild != null) {
            return guild.getTextChannels().stream().map(guildChannel -> new DiscordChannel(this, guildChannel)).collect(Collectors.toList());
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
    public Channel getChannel(final long channelId) throws BotHomeError {
        TextChannel channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            throw new BotHomeError("No channel with id %d.", channelId);
        }
        return new DiscordChannel(this, channel);
    }

    @Override
    public List<Role> getRoles() {
        if (guild == null) {
            return Collections.emptyList();
        }
        return guild.getRoles().stream().filter(role -> !role.isManaged() && !role.isPublicRole()).map(DiscordRole::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getRole(final User user) {
        Member member = guild.getMemberById(getDiscordId(user));
        List<net.dv8tion.jda.api.entities.Role> roles = member.getRoles();
        if (!roles.isEmpty()) {
            return roles.get(0).getName();
        }
        return "Pleb";
    }

    @Override
    public boolean hasRole(final User user, final long roleId) {
        Member member = getMember(user);
        return member.getRoles().stream().anyMatch(role -> role.getIdLong() == roleId);
    }

    @Override
    public void clearRole(final User user, final String roleName) throws UserError {
        Member member = getMember(user);
        guild.removeRoleFromMember(member, getRole(roleName)).queue();
    }

    @Override
    public void clearRole(final User user, final long roleId) throws BotHomeError {
        Member member = getMember(user);
        guild.removeRoleFromMember(member, getRoleInternal(roleId)).queue();
    }

    @Override
    public void setRole(final User user, final String roleName) throws UserError {
        guild.addRoleToMember(getMember(user), getRole(roleName)).queue();
    }

    @Override
    public void setRole(final User user, final long roleId) throws BotHomeError {
        Member member = getMember(user);
        guild.addRoleToMember(member, getRoleInternal(roleId)).queue();
    }

    @Override
    public List<String> clearRole(final String roleName) throws UserError {
        net.dv8tion.jda.api.entities.Role role = getRole(roleName);
        List<Member> members = guild.getMembersWithRoles(role);
        List<String> names = new ArrayList<>();
        for (Member member : members) {
            guild.removeRoleFromMember(member, role).queue();
            names.add(member.getEffectiveName());
        }
        return names;
    }

    @Override
    public boolean isHigherRanked(final User user, final User otherUser) {
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
    public User getUser(final long discordId, final String userName) {
        Member member = guild.getMemberById(discordId);
        HomedUser homedUser = getHomeEditor().getUserByDiscordId(discordId, userName);
        return new DiscordHomedUser(homedUser, member);
    }

    @Override
    public User getUser(final String userName) throws UserError {
        Member member = getMember(userName);
        return getUser(member.getIdLong(), member.getEffectiveName());
    }

    @Override
    public User getUser(final HomedUser homedUser) {
        Member member = guild.getMemberById(homedUser.getDiscordId());
        return new DiscordHomedUser(homedUser, member);
    }

    @Override
    public void setNickName(final User user, final String nickName) {
        Member member = guild.getMemberById(user.getHomedUser().getDiscordId());
        member.modifyNickname(nickName).queue();
    }

    @Override
    public Message getSavedMessage(final long channelId, final long messageId) {
        return new DiscordSavedMessage(this, channelId, messageId);
    }

    public DiscordMessage getMessage(final long channelId, final long messageId) {
        net.dv8tion.jda.api.entities.Message message =
                guild.getTextChannelById(channelId).retrieveMessageById(messageId).complete();
        Member sender = message.getMember();
        return new DiscordMessage(getUser(sender.getIdLong(), sender.getEffectiveName()), message);
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
            cachedEmotes = emotes.stream().map(DiscordEmote::new).collect(Collectors.toList());
        } else {
            cachedEmotes = new ArrayList<>();
        }
    }

    private long getDiscordId(final User user) {
        return ((DiscordHomedUser) user).getDiscordId();
    }

    private Member getMember(final User user) {
        return ((DiscordHomedUser)user).getMember();
    }

    private Member getMember(final String username) throws UserError {
        if (Strings.isBlank(username)) {
            throw new UserError("No one specified.");
        }

        List<Member> members = guild.getMembersByEffectiveName(deamp(username), true);
        if (!members.isEmpty()) {
            return members.get(0);
        }
        members = guild.getMembersByName(deamp(username), true);
        if (!members.isEmpty()) {
            return members.get(0);
        }
        throw new UserError(String.format("No user named '%s'.", deamp(username)));
    }

    private String deamp(final String string) {
        if (string.startsWith("@")) {
            return string.substring(1);
        }
        return string;
    }

    private net.dv8tion.jda.api.entities.Role getRole(final String roleName) throws UserError {
        List<net.dv8tion.jda.api.entities.Role> roles = guild.getRolesByName(deamp(roleName), false);
        if (roles.isEmpty()) {
            throw new UserError(String.format("'%s' is not a valid role.", roleName));
        }
        return roles.get(0);
    }

    @Override
    public Role getRole(final long roleId) throws BotHomeError {
        return new DiscordRole(getRoleInternal(roleId));
    }

    private net.dv8tion.jda.api.entities.Role getRoleInternal(final long roleId) throws BotHomeError {
        net.dv8tion.jda.api.entities.Role role = guild.getRoleById(roleId);
        if (role == null) {
            throw new BotHomeError(String.format("'%s' is not a valid role id.", roleId));
        }
        return role;
    }

    private int getPosition(final Member member) {
        int position = -1;
        for (net.dv8tion.jda.api.entities.Role role : member.getRoles()) {
            position = Math.max(position, role.getPosition());
        }
        return position;
    }
}
