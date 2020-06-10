package com.ryan_mtg.servobot.twitch.model;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.StreamList;
import com.ryan_mtg.servobot.data.factories.LoggedMessageSerializer;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.twitch.event.TwitchEventGenerator;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.utility.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TwitchService implements Service {
    public static final int TYPE = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchService.class);

    private String clientId;
    private String secret;
    private String oauthToken;
    private String authToken;
    private TwitchEventGenerator generator;
    private StreamStartRegulator regulator;
    private Map<Long, BotHome> homeMap = new HashMap<>();
    private Map<Long, String> channelNameMap = new HashMap<>();
    private Map<Long, String> channelImageMap = new HashMap<>();
    private TwitchClient client;
    private ScheduledExecutorService executorService;
    private LoggedMessageSerializer loggedMessageSerializer;

    public TwitchService(final String clientId, final String secret, final String oauthToken,
            final ScheduledExecutorService executorService, final LoggedMessageSerializer loggedMessageSerializer)
            throws UserError {
        this.clientId = clientId;
        this.secret = secret;
        this.oauthToken = oauthToken;
        this.authToken = oauthToken.substring(oauthToken.indexOf(':') + 1);
        this.executorService = executorService;
        this.regulator = new StreamStartRegulator(this, homeMap);
        this.loggedMessageSerializer = loggedMessageSerializer;

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

    @Override
    public String getBotName() {
        return getUserInfo(authToken).getUsername();
    }

    @Override
    public String getImageUrl() {
        return getChannelImageUrl(getUserInfo(authToken).getId());
    }

    public TwitchUserInfo getUserInfo(final String auth) {
        com.github.twitch4j.helix.domain.User user =
                client.getHelix().getUsers(auth, null, null).execute().getUsers().get(0);
        return new TwitchUserInfo(Integer.parseInt(user.getId()), user.getLogin());
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
            TwitchServiceHome twitchServiceHome = (TwitchServiceHome) serviceHome;
            homeMap.put(twitchServiceHome.getChannelId(), botHome);
            regulator.addHome(twitchServiceHome);
        }
    }

    public Channel getChannel(final String channelName) {
        return new TwitchChannelOnly(client.getChat(), channelName);
    }

    @Override
    public void unregister(final BotHome botHome) {
        ServiceHome serviceHome = botHome.getServiceHome(TwitchService.TYPE);
        if (serviceHome != null) {
            TwitchServiceHome twitchServiceHome = (TwitchServiceHome) serviceHome;
            regulator.removeHome(twitchServiceHome);
            homeMap.remove(twitchServiceHome.getChannelId());
        }
    }

    @Override
    public void start(final EventListener eventListener) {
        OAuth2Credential credential = new OAuth2Credential("twitch", oauthToken);
        client = TwitchClientBuilder.builder().withEnableHelix(true).withEnableChat(true)
                .withClientId(clientId).withClientSecret(secret).withChatAccount(credential).build();

        generator = new TwitchEventGenerator(client, eventListener, homeMap);
        regulator.start(client, eventListener);
        executorService.scheduleAtFixedRate(regulator, 60, 30, TimeUnit.SECONDS);
        homeMap.forEach((channelId, home) -> LOGGER.info("{} streaming: {}", channelId, isStreaming(channelId)));

        client.getChat().sendPrivateMessage("ryan_mtg", "hello punk");
    }

    public Set<Long> getChannelsStreaming(final List<Long> channelIds) {
        List<String> channelIdStrings = channelIds.stream().map(id -> Long.toString(id)).collect(Collectors.toList());
        StreamList streamList = client.getHelix().getStreams(authToken, "", null, null,null, null, null,
                channelIdStrings, null).execute();

        Set<Long> streamingIds = new HashSet<>();
        streamList.getStreams().forEach(stream -> {
            long id = Long.parseLong(stream.getUserId());
            streamingIds.add(id);
        });
        return streamingIds;
    }

    @Override
    public void whisper(final User user, final String message) {
        loggedMessageSerializer.logSentMessage(user, message, TwitchService.TYPE);
        throw new RuntimeException("Not supported");
    }

    public Home getHome(final long channelId, final HomeEditor homeEditor) {
        return new TwitchChannel(client, getChannelName(channelId), homeEditor);
    }

    public boolean isStreaming(final long channelId) {
        StreamList streamList = client.getHelix().getStreams(authToken, "", null, null,null, null, null,
                Collections.singletonList(Long.toString(channelId)), null).execute();
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
                getUsers(authToken, Collections.singletonList(Long.toString(channelId)), null).execute()
                .getUsers().get(0);
    }
}
