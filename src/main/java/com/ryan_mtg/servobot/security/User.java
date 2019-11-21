package com.ryan_mtg.servobot.security;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.security.Principal;

public class User implements Principal {
    private OAuth2AuthenticationToken oAuth2AuthenticationToken;

    public User(final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
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
}
