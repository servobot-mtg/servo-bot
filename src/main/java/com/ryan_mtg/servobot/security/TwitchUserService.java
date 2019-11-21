package com.ryan_mtg.servobot.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TwitchUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private DefaultOAuth2UserService userService;

    public TwitchUserService() {
        userService = new DefaultOAuth2UserService();
    }

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = userService.loadUser(userRequest);
        Map<String, Object> attributes = user.getAttributes();
        Map<String, Object> newAttributes = (Map<String, Object>) (((ArrayList) attributes.get("data")).get(0));

        AuthorityUtils.createAuthorityList("ROLE_USER");
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
        return new DefaultOAuth2User(user.getAuthorities(), newAttributes, "login");
    }
}
