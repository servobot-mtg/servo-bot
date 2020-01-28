package com.ryan_mtg.servobot.security;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TwitchUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private DefaultOAuth2UserService userService;
    private UserSerializer userSerializer;

    public TwitchUserService(final UserSerializer userSerializer) {
        userService = new DefaultOAuth2UserService();
        this.userSerializer = userSerializer;
    }

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = userService.loadUser(userRequest);

        Map<String, Object> outerAttributes = oAuth2User.getAttributes();
        @SuppressWarnings("unchecked")
        Map<String, Object> attributes = (Map<String, Object>) (((ArrayList) outerAttributes.get("data")).get(0));

        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList("ROLE_USER");

        int twitchId = Integer.parseInt(attributes.get("id").toString());
        User user = userSerializer.lookupByTwitchId(twitchId, attributes.get("display_name").toString());
        if (user.isAdmin()) {
            authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        for (Integer homeId : userSerializer.getHomesModerated(user.getId())) {
            authorityList.add(new SimpleGrantedAuthority(String.format("ROLE_MOD:%d", homeId)));
        }

        for (Integer homeId : userSerializer.getHomesStreamed(user.getId())) {
            authorityList.add(new SimpleGrantedAuthority(String.format("ROLE_STREAMER:%d", homeId)));
        }

        attributes.put("oauth_token", userRequest.getAccessToken().getTokenValue());

        return new DefaultOAuth2User(authorityList, attributes, "display_name");
    }
}
