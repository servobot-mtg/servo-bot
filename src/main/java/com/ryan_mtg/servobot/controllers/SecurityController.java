package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.security.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/login")
    public String login() {
        return "redirect:/";
    }
}
