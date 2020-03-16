package com.ryan_mtg.servobot.security;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class WebsiteUserFactory {
    @Autowired
    private UserSerializer userSerializer;

    public WebsiteUser createWebsiteUser(final Authentication authentication) {
        User user = null;
        OAuth2AuthenticationToken oAuth2AuthenticationToken = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication instanceof OAuth2AuthenticationToken) {
            oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            int userId = (int) oAuth2AuthenticationToken.getPrincipal().getAttributes()
                    .get(TwitchUserService.USER_ID_PROPERTY);
            try {
                user = userSerializer.lookupById(userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new WebsiteUser(userSerializer, oAuth2AuthenticationToken, user);
    }
}
