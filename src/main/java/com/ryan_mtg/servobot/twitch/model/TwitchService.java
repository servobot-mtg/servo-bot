package com.ryan_mtg.servobot.twitch.model;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
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

    private String clientId;
    private String secret;
    private String oauthToken;
    private TwitchEventGenerator generator;
    private Map<Long, Integer> homeIdMap = new HashMap<>();
    private Map<Long, String> channelNameMap = new HashMap<>();
    private TwitchClient client;

    public TwitchService(final String clientId, final String secret, final String oauthToken) {
        this.clientId = clientId;
        this.secret = secret;
        this.oauthToken = oauthToken;
    }

    @Override
    public String getName() {
        return "Twitch";
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
                .withClientId(clientId).withClientSecret(secret).withChatAccount(credential).build();

        generator = new TwitchEventGenerator(client, eventListener, homeIdMap);
    }

    public Home getHome(final long channelId) {
        return new TwitchChannel(client.getChat(), getChannelName(channelId));
    }

    public String getChannelName(final long channelId) {
        if (channelNameMap.containsKey(channelId)) {
            return channelNameMap.get(channelId);
        }

        String channelName = fetchChannelName(channelId);
        channelNameMap.put(channelId, channelName);
        return channelName;
    }

    public void joinChannel(final long channelId) {
        String channelName = getChannelName(channelId);
        client.getChat().joinChannel(channelName);
    }

    private String fetchChannelName(final long channelId) {
        return client.getHelix().
                getUsers(null, Arrays.asList(Long.toString(channelId)), null).execute()
                .getUsers().get(0).getLogin();
    }
}
