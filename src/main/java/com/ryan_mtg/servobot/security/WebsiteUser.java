package com.ryan_mtg.servobot.security;

import com.google.common.collect.Lists;
import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

public class WebsiteUser implements Principal {
    private UserSerializer userSerializer;
    private OAuth2AuthenticationToken oAuth2AuthenticationToken;
    private User user;

    public WebsiteUser(final UserSerializer userSerializer, final OAuth2AuthenticationToken oAuth2AuthenticationToken, final User user) {
        this.userSerializer = userSerializer;
        this.oAuth2AuthenticationToken = oAuth2AuthenticationToken;
        this.user = user;
    }

    @Override
    public String getName() {
        return oAuth2AuthenticationToken.getName();
    }

    public boolean isAuthenticated() {
        if (oAuth2AuthenticationToken == null) {
            return false;
        }
        return oAuth2AuthenticationToken.isAuthenticated();
    }

    public boolean isAdmin() {
        return user != null ? user.isAdmin() : false;
    }

    public boolean hasInvite() {
        return user != null ? user.hasInvite() : false;
    }

    public boolean isAStreamer() {
        if (oAuth2AuthenticationToken == null) {
            return false;
        }

        return oAuth2AuthenticationToken.getPrincipal().getAuthorities().stream()
                .filter(authority -> authority.getAuthority().startsWith("ROLE_STREAMER")).findAny().isPresent();
    }

    public int getBotHomeId() {
        for(GrantedAuthority authority : oAuth2AuthenticationToken.getPrincipal().getAuthorities()) {
            final String authorityString = authority.getAuthority();
            if (authorityString.startsWith("ROLE_STREAMER")) {
                return Integer.parseInt(authorityString.substring("ROLE_STREAMER:".length()));
            }
        }
        return 0;
    }

    public List<String> getRoles() {
        if (oAuth2AuthenticationToken == null) {
            return Lists.newArrayList();
        }

        return oAuth2AuthenticationToken.getPrincipal().getAuthorities().stream()
                .map(authority -> authority.getAuthority()).collect(Collectors.toList());
    }

    public boolean isPrivledged() {
        if (oAuth2AuthenticationToken == null || !oAuth2AuthenticationToken.isAuthenticated()) {
            return false;
        }

        if (isAdmin()) {
            return true;
        }

        if (isAStreamer()) {
            return true;
        }

        if (!userSerializer.getHomesModerated(getUserId()).isEmpty()) {
            return true;
        }

        return false;
    }


    public boolean isPrivledged(final BotHome botHome) {
        if (oAuth2AuthenticationToken == null || !oAuth2AuthenticationToken.isAuthenticated()) {
            return false;
        }

        if (isAdmin()) {
            return true;
        }

        if (isAStreamer() && getBotHomeId() == botHome.getId()) {
            return true;
        }

        if (userSerializer.getHomesModerated(getUserId()).contains(botHome.getId())) {
            return true;
        }

        return false;
    }

    private int getUserId() {
        return user != null ? user.getId() : User.UNREGISTERED_ID;
    }
}
