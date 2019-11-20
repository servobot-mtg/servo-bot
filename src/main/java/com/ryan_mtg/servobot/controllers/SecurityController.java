package com.ryan_mtg.servobot.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class SecurityController {
    @RequestMapping("/user")
    @ResponseBody
    public Principal user(final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return new User(oAuth2AuthenticationToken);
    }

    private static class User implements Principal {
        private OAuth2AuthenticationToken oAuth2AuthenticationToken;

        public User(final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            this.oAuth2AuthenticationToken = oAuth2AuthenticationToken;
        }

        @Override
        public String getName() {
            return oAuth2AuthenticationToken.getName();
        }

        public boolean isAuthenticated() {
            return oAuth2AuthenticationToken.isAuthenticated();
        }
    }

}
