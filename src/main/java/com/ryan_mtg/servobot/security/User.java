package com.ryan_mtg.servobot.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

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

    public boolean isAdmin() {
        if (oAuth2AuthenticationToken == null) {
            return false;
        }

        return oAuth2AuthenticationToken.getPrincipal().getAuthorities().stream()
                .filter(authority -> authority.getAuthority().equals("ROLE_ADMIN")).findAny().isPresent();
    }
}
