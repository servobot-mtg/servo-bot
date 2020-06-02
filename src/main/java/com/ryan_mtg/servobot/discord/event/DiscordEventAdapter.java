package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.data.factories.LoggedMessageSerializer;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.discord.model.DiscordUser;
import com.ryan_mtg.servobot.discord.model.DiscordUserStatus;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserTable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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

    private final EventListener listener;
    private final Map<Long, BotHome> homeMap;
    private final StreamStartRegulator streamStartRegulator;
    private final UserTable userTable;
    private final LoggedMessageSerializer loggedMessageSerializer;

    public DiscordEventAdapter(final EventListener listener, final Map<Long, BotHome> homeMap,
            final StreamStartRegulator streamStartRegulator, final UserTable userTable,
            final LoggedMessageSerializer loggedMessageSerializer) {
        this.listener = listener;
        this.homeMap = homeMap;
        this.streamStartRegulator = streamStartRegulator;
        this.userTable = userTable;
        this.loggedMessageSerializer = loggedMessageSerializer;
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull final PrivateMessageReceivedEvent event) {
        try {
            net.dv8tion.jda.api.entities.User author = event.getAuthor();
            if (event.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
                return;
            }

            User sender = userTable.getByDiscordId(author.getIdLong(), author.getName());
            loggedMessageSerializer.logReceivedMessage(sender, event.getMessage().getContentRaw(), DiscordService.TYPE);
        } catch (BotErrorException e) {
            LOGGER.warn("Unhandled BotErrorException: {}", e.getErrorMessage());
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull final GuildMessageReceivedEvent event) {
        try {
            BotHome botHome = homeMap.get(event.getGuild().getIdLong());
            if (botHome == null) {
                return;
            }
            DiscordUser sender = getUser(event.getMember(), botHome);
            listener.onMessage(new DiscordMessageSentEvent(event, botHome, sender));
        } catch (BotErrorException e) {
            LOGGER.warn("Unhandled BotErrorException: {}", e.getErrorMessage());
        }
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
        try {
            BotHome botHome = homeMap.get(event.getGuild().getIdLong());
            if (botHome == null) {
                return;
            }
            if (streamStartRegulator.startActivity(event, botHome.getId())) {
                //listener.onStreamStart(new DiscordStreamStartEvent(event, botHome.getId()));
            }
        } catch (Exception e) {
            LOGGER.error("Error during activity start", e);
        }
    }

    @Override
    public void onGuildMemberJoin(@Nonnull final GuildMemberJoinEvent event) {
        try {
            BotHome botHome = homeMap.get(event.getGuild().getIdLong());
            if (botHome == null) {
                return;
            }
            DiscordUser member = getUser(event.getMember(), botHome);
            listener.onNewUser(new DiscordNewUserEvent(event, botHome, member));
        } catch (BotErrorException e) {
            LOGGER.warn("Unhandled BotErrorException: {}", e.getErrorMessage());
        }
    }

    private DiscordUser getUser(final Member member, final BotHome botHome) throws BotErrorException {
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
