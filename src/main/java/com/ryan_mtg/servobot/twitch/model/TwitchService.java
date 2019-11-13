package com.ryan_mtg.servobot.twitch.model;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.UserList;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.twitch.event.TwitchEventGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TwitchService implements Service {
    public static final int TYPE = 1;

    private String oauthToken;
    private TwitchEventGenerator generator;
    private Map<Long, Integer> homeIdMap = new HashMap<>();
    private Map<Long, String> channelNameMap = new HashMap<>();
    private TwitchClient client;

    public TwitchService(final String oauthToken) {
        this.oauthToken = oauthToken;
    }

    @Override
    public void register(final BotHome botHome) {
        ServiceHome serviceHome = botHome.getServiceHome(TwitchService.TYPE);
        if (serviceHome != null) {
            homeIdMap.put(((TwitchServiceHome) serviceHome).getChannelId(), botHome.getId());
        }
    }

    @Override
    public void start(final EventListener eventListener) throws Exception {
        OAuth2Credential credential = new OAuth2Credential("twitch", oauthToken);
        client = TwitchClientBuilder.builder().withEnableHelix(true).withEnableChat(true)
                .withChatAccount(credential).build();
        for (long channelId : homeIdMap.keySet()) {
            UserList userList = client.getHelix().
                    getUsers(oauthToken, Arrays.asList(Long.toString(channelId)), null).execute();
            String channelName = userList.getUsers().get(0).getLogin();
            client.getChat().joinChannel(channelName);
        }

        generator = new TwitchEventGenerator(client, eventListener, homeIdMap);
    }

    @Override
    public Home getHome(final ServiceHome serviceHome) {
        return new TwitchChannel(client.getChat(), getChannelName(((TwitchServiceHome) serviceHome).getChannelId()));
    }

    private String getChannelName(final long channelId) {
        if (channelNameMap.containsKey(channelId)) {
            return channelNameMap.get(channelId);
        }

        String channelName = client.getHelix().
                getUsers(oauthToken, Arrays.asList(Long.toString(channelId)), null).execute()
                .getUsers().get(0).getLogin();
        channelNameMap.put(channelId, channelName);
        return channelName;
    }
}
