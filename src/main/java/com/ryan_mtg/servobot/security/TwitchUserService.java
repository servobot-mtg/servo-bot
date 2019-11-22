package com.ryan_mtg.servobot.security;

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
import java.util.HashSet;
import java.util.List;
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

        Map<String, Object> outerAttributes = user.getAttributes();
        @SuppressWarnings("unchecked")
        Map<String, Object> attributes = (Map<String, Object>) (((ArrayList) outerAttributes.get("data")).get(0));

        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList("ROLE_USER");

        String userName = (String)attributes.get("login");
        if (userName.equals("ryan_mtg")) {
            authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
        return new DefaultOAuth2User(authorityList, attributes, "login");
    }
}
