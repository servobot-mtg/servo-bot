package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.common.events.domain.EventUser;
import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.twitch.model.TwitchUser;
import com.ryan_mtg.servobot.twitch.model.TwitchUserStatus;
import com.ryan_mtg.servobot.user.User;

import java.util.Map;
import java.util.Set;

public class TwitchEventGenerator {
    private EventListener eventListener;
    private Map<Long, Integer> homeIdMap;
    private UserSerializer userSerializer;

    public TwitchEventGenerator(final TwitchClient client, final EventListener eventListener,
                                final Map<Long, Integer> homeIdMap, final UserSerializer userSerializer) {
        this.eventListener = eventListener;
        this.homeIdMap = homeIdMap;
        this.userSerializer = userSerializer;

        client.getEventManager().onEvent(ChannelMessageEvent.class).subscribe(event -> {
            int botHomeId = resolveBotHomeId(event.getChannel().getId());
            TwitchUser sender = getUser(event.getUser(), event.getPermissions(), botHomeId);
            eventListener.onMessage(new TwitchMessageSentEvent(event, botHomeId, sender));
        });
    }

    private int resolveBotHomeId(final String channelId) {
        return homeIdMap.get(Long.parseLong(channelId));
    }

    private TwitchUser getUser(final EventUser eventUser, final Set<CommandPermission> permissions,
                               final int botHomeId) {
        User user = userSerializer.lookupByTwitchId(Integer.parseInt(eventUser.getId()), eventUser.getName());
        boolean isModerator = permissions.contains(CommandPermission.MODERATOR);
        boolean isSubscriber = permissions.contains(CommandPermission.SUBSCRIBER);
        TwitchUserStatus status = new TwitchUserStatus(isModerator, isSubscriber);
        userSerializer.updateStatus(user, botHomeId, status);
        return new TwitchUser(user, status);
    }
}
