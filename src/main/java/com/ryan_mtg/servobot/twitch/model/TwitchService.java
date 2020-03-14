package com.ryan_mtg.servobot.twitch.model;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.StreamList;
import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.twitch.event.TwitchEventGenerator;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.utility.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TwitchService implements Service {
    public static final int TYPE = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchService.class);

    private String clientId;
    private String secret;
    private String oauthToken;
    private TwitchEventGenerator generator;
    private Map<Long, Integer> homeIdMap = new HashMap<>();
    private Map<Long, String> channelNameMap = new HashMap<>();
    private Map<Long, String> channelImageMap = new HashMap<>();
    private TwitchClient client;
    private UserSerializer userSerializer;

    public TwitchService(final String clientId, final String secret, final String oauthToken,
                         final UserSerializer userSerializer) throws BotErrorException {
        this.clientId = clientId;
        this.secret = secret;
        this.oauthToken = oauthToken;
        this.userSerializer = userSerializer;

        Validation.validateStringLength(clientId, Validation.MAX_CLIENT_ID_LENGTH, "Client id");
        Validation.validateStringLength(secret, Validation.MAX_CLIENT_SECRET_LENGTH, "Client secret");
        Validation.validateStringLength(oauthToken, Validation.MAX_AUTHENTICATION_TOKEN_LENGTH, "OAuthToken");
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return "Twitch";
    }

    public String getClientId() {
        return clientId;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public void register(final BotHome botHome) {
        ServiceHome serviceHome = botHome.getServiceHome(TwitchService.TYPE);
        if (serviceHome != null) {
            homeIdMap.put(((TwitchServiceHome) serviceHome).getChannelId(), botHome.getId());
        }
    }

    @Override
    public void unregister(final BotHome botHome) {
        ServiceHome serviceHome = botHome.getServiceHome(TwitchService.TYPE);
        if (serviceHome != null) {
            homeIdMap.remove(((TwitchServiceHome) serviceHome).getChannelId());
        }
    }

    @Override
    public void start(final EventListener eventListener) {
        OAuth2Credential credential = new OAuth2Credential("twitch", oauthToken);

        client = TwitchClientBuilder.builder().withEnableHelix(true).withEnableChat(true)
                .withClientId(clientId).withClientSecret(secret).withChatAccount(credential).build();

        client.getChat().sendPrivateMessage("ryan_mtg", "hello punk");

        generator = new TwitchEventGenerator(client, eventListener, homeIdMap, userSerializer);

        homeIdMap.forEach((channelId, homeId) -> LOGGER.info("{} streaming: {}", channelId, isStreaming(channelId)));
    }

    @Override
    public void whisper(final User user, final String message) {
        throw new RuntimeException("Not supported");
    }

    public Home getHome(final long channelId, final HomeEditor homeEditor) {
        return new TwitchChannel(client, getChannelName(channelId), homeEditor);
    }

    public boolean isStreaming(final long channelId) {
        StreamList streamList = client.getHelix().getStreams(null, "", null, null,null, null, null,
                Arrays.asList(Long.toString(channelId)), null).execute();
        return !streamList.getStreams().isEmpty();
    }

    public String getChannelImageUrl(final long channelId) {
        if (channelImageMap.containsKey(channelId)) {
            return channelImageMap.get(channelId);
        }

        String channelImage = fetchChannelImageUrl(channelId);
        channelImageMap.put(channelId, channelImage);
        return channelImage;
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

    public void leaveChannel(final long channelId) {
        String channelName = getChannelName(channelId);
        client.getChat().leaveChannel(channelName);
    }

    private String fetchChannelImageUrl(final long channelId) {
        return fetchChannelUser(channelId).getProfileImageUrl();
    }

    private String fetchChannelName(final long channelId) {
        return fetchChannelUser(channelId).getLogin();
    }

    private com.github.twitch4j.helix.domain.User fetchChannelUser(final long channelId) {
        return client.getHelix().
                getUsers(null, Collections.singletonList(Long.toString(channelId)), null).execute()
                .getUsers().get(0);
    }
}
