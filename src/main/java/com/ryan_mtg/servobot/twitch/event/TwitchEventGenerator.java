package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.common.events.domain.EventUser;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.twitch.model.TwitchUser;
import com.ryan_mtg.servobot.twitch.model.TwitchUserStatus;
import com.ryan_mtg.servobot.user.HomedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class TwitchEventGenerator {
    private static Logger LOGGER = LoggerFactory.getLogger(TwitchEventGenerator.class);
    private TwitchClient client;
    private EventListener eventListener;
    private Map<Long, BotHome> homeMap;

    public TwitchEventGenerator(final TwitchClient client, final EventListener eventListener,
                                final Map<Long, BotHome> homeMap) {
        this.client = client;
        this.eventListener = eventListener;
        this.homeMap = homeMap;

        client.getEventManager().onEvent(ChannelMessageEvent.class).subscribe(this::handleMessageEvent);
    }

    private void handleMessageEvent(final ChannelMessageEvent event) {
        try {
            BotHome botHome = resolveBotHomeId(event.getChannel().getId());
            TwitchUser sender = getUser(event.getTwitchChat(), event.getUser(), event.getPermissions(), botHome,
                    event.getChannel().getName());
            eventListener.onMessage(new TwitchMessageSentEvent(client, event, botHome.getId(), sender));
        } catch (BotErrorException e) {
            LOGGER.warn("Unhandled BotErrorException: {}", e.getErrorMessage());
        } catch (Exception e) {
            LOGGER.warn("Unhandled ErrorException: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private BotHome resolveBotHomeId(final String channelId) {
        return homeMap.get(Long.parseLong(channelId));
    }

    private TwitchUser getUser(final TwitchChat chat, final EventUser eventUser,
                               final Set<CommandPermission> permissions, final BotHome botHome,
                               final String channelName) throws BotErrorException {
        boolean isModerator = permissions.contains(CommandPermission.MODERATOR);
        boolean isSubscriber = permissions.contains(CommandPermission.SUBSCRIBER);
        boolean isVip = permissions.contains(CommandPermission.VIP);
        boolean isStreamer = channelName.equalsIgnoreCase(eventUser.getName());
        TwitchUserStatus status = new TwitchUserStatus(isModerator, isSubscriber, isVip, isStreamer);

        HomedUser user = botHome.getHomedUserTable().getByTwitchId(Integer.parseInt(eventUser.getId()),
                eventUser.getName(), status);
        return new TwitchUser(chat, user);
    }
}
