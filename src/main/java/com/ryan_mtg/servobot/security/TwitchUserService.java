package com.ryan_mtg.servobot.security;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.model.UserInfo;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.twitch.model.TwitchUserInfo;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.user.UserTable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitchUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    public static final String USER_ID_PROPERTY = "servobot:userId";
    private final UserTable userTable;
    private final TwitchService twitchService;
    private final DiscordService discordService;

    public TwitchUserService(final TwitchService twitchService, final DiscordService discordService,
                             final UserTable userTable) {
        this.twitchService = twitchService;
        this.discordService = discordService;
        this.userTable = userTable;
    }

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = new HashMap<>();

        User user;
        String registrar = userRequest.getClientRegistration().getRegistrationId();
        if (registrar.equals("twitch")) {
            UserInfo userInfo = twitchService.getUserInfo(userRequest.getAccessToken().getTokenValue());
            user = userTable.getByTwitchId((int) userInfo.getId(), userInfo.getUsername());
        } else if (registrar.equals("discord")) {
            UserInfo userInfo = discordService.getUserInfo(userRequest.getAccessToken().getTokenValue());
            user = userTable.getByDiscordId(userInfo.getId(), userInfo.getUsername());
        } else {
            throw new SystemError("Unknown service: %s", registrar);
        }

        List<GrantedAuthority> authorityList =
                AuthorityUtils.createAuthorityList("ROLE_USER", String.format("ROLE_ID:%d", user.getId()));

        if (user.isAdmin()) {
            authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        if (user.isEditor()) {
            authorityList.add(new SimpleGrantedAuthority("ROLE_EDITOR"));
        }

        for (Integer homeId : userTable.getHomesModerated(user.getId())) {
            authorityList.add(new SimpleGrantedAuthority(String.format("ROLE_MOD:%d", homeId)));
        }

        for (Integer homeId : userTable.getHomesStreamed(user.getId())) {
            authorityList.add(new SimpleGrantedAuthority(String.format("ROLE_STREAMER:%d", homeId)));
        }

        attributes.put(USER_ID_PROPERTY, user.getId());
        attributes.put("oauth_token", userRequest.getAccessToken().getTokenValue());
        attributes.put("name", user.getName());

        return new DefaultOAuth2User(authorityList, attributes, "name");
    }
}
