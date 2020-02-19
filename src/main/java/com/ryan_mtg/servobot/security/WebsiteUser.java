package com.ryan_mtg.servobot.security;

import com.google.common.collect.Lists;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

public class WebsiteUser implements Principal {
    private OAuth2AuthenticationToken oAuth2AuthenticationToken;

    public WebsiteUser(final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        this.oAuth2AuthenticationToken = oAuth2AuthenticationToken;
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
        if (oAuth2AuthenticationToken == null) {
            return false;
        }

        return oAuth2AuthenticationToken.getPrincipal().getAuthorities().stream()
                .filter(authority -> authority.getAuthority().equals("ROLE_ADMIN")).findAny().isPresent();
    }

    public boolean isAStreamer() {
        if (oAuth2AuthenticationToken == null) {
            return false;
        }

        return oAuth2AuthenticationToken.getPrincipal().getAuthorities().stream()
                .filter(authority -> authority.getAuthority().startsWith("ROLE_STREAMER")).findAny().isPresent();
    }

    public List<String> getRoles() {
        if (oAuth2AuthenticationToken == null) {
            return Lists.newArrayList();
        }

        return oAuth2AuthenticationToken.getPrincipal().getAuthorities().stream()
                .map(authority -> authority.getAuthority()).collect(Collectors.toList());
    }
}
