package com.ryan_mtg.servobot.security;

import com.ryan_mtg.servobot.user.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class WebsiteUserFactory {
    public WebsiteUser createWebsiteUser(final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        User user = null;
        if (oAuth2AuthenticationToken != null && oAuth2AuthenticationToken.isAuthenticated()) {
            user = (User) oAuth2AuthenticationToken.getPrincipal().getAttributes().get(TwitchUserService.USER_PROPERTY);
        }
        return new WebsiteUser(oAuth2AuthenticationToken, user);
    }
}
