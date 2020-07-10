package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.data.factories.LoggedMessageSerializer;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.discord.model.DiscordUser;
import com.ryan_mtg.servobot.discord.model.DiscordUserStatus;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserTable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;

public class DiscordEventAdapter extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordEventAdapter.class);

    private final DiscordService discordService;
    private final EventListener listener;
    private final Map<Long, BotHome> homeMap;
    private final StreamStartRegulator streamStartRegulator;
    private final UserTable userTable;
    private final LoggedMessageSerializer loggedMessageSerializer;

    public DiscordEventAdapter(final DiscordService discordService, final EventListener listener,
            final Map<Long, BotHome> homeMap, final StreamStartRegulator streamStartRegulator,
            final UserTable userTable, final LoggedMessageSerializer loggedMessageSerializer) {
        this.discordService = discordService;
        this.listener = listener;
        this.homeMap = homeMap;
        this.streamStartRegulator = streamStartRegulator;
        this.userTable = userTable;
        this.loggedMessageSerializer = loggedMessageSerializer;
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull final PrivateMessageReceivedEvent event) {
        net.dv8tion.jda.api.entities.User author = event.getAuthor();
        if (event.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
            return;
        }

        User sender = userTable.getByDiscordId(author.getIdLong(), author.getName());
        loggedMessageSerializer.logReceivedMessage(sender, event.getMessage().getContentRaw(), DiscordService.TYPE);

        DiscordGlobalUser discordSender = new DiscordGlobalUser(author, sender);
        listener.onPrivateMessage(new DiscordPrivateMessageEvent(discordService, event, discordSender));
    }

    @Override
    public void onGuildMessageReceived(@Nonnull final GuildMessageReceivedEvent event) {
        BotHome botHome = homeMap.get(event.getGuild().getIdLong());
        if (botHome == null) {
            return;
        }
        DiscordUser sender = getUser(event.getMember(), botHome);
        listener.onMessage(new DiscordMessageSentEvent(event, botHome, sender));
    }

    @Override
    public void onUserActivityEnd(@Nonnull final UserActivityEndEvent event) {
        BotHome botHome = homeMap.get(event.getGuild().getIdLong());
        if (botHome == null) {
            return;
        }
        streamStartRegulator.endActivity(event, botHome.getId());
    }

    @Override
    public void onUserActivityStart(@Nonnull final UserActivityStartEvent event) {
        BotHome botHome = homeMap.get(event.getGuild().getIdLong());
        if (botHome == null) {
            return;
        }
        if (streamStartRegulator.startActivity(event, botHome.getId())) {
            //listener.onStreamStart(new DiscordStreamStartEvent(event, botHome.getId()));
        }
    }

    @Override
    public void onGuildMemberJoin(@Nonnull final GuildMemberJoinEvent event) {
        BotHome botHome = homeMap.get(event.getGuild().getIdLong());
        if (botHome == null) {
            return;
        }
        DiscordUser member = getUser(event.getMember(), botHome);
        listener.onNewUser(new DiscordNewUserEvent(event, botHome, member));
    }

    //Emote Events
    @Override
    public void onEmoteAdded(@Nonnull EmoteAddedEvent event) {
        BotHome botHome = homeMap.get(event.getGuild().getIdLong());
        if (botHome == null) {
            return;
        }

        botHome.getServiceHome(DiscordService.TYPE).updateEmotes();
    }

    @Override
    public void onEmoteRemoved(@Nonnull EmoteRemovedEvent event) {
        BotHome botHome = homeMap.get(event.getGuild().getIdLong());
        if (botHome == null) {
            return;
        }
        botHome.getServiceHome(DiscordService.TYPE).updateEmotes();
    }

    private DiscordUser getUser(final Member member, final BotHome botHome) {
        boolean isModerator = false;
        for(Role role : member.getRoles()) {
            if (role.getPermissions().contains(Permission.KICK_MEMBERS)) {
                isModerator = true;
            }
        }

        DiscordUserStatus status = new DiscordUserStatus(isModerator, false, member.isOwner());
        HomedUser user = botHome.getHomedUserTable().getByDiscordId(member.getIdLong(), member.getEffectiveName(),
                status);
        return new DiscordUser(user, member);
    }
}
