package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.security.User;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BotController {
    @Autowired
    @Qualifier("bot")
    private Bot bot;

    @GetMapping("/")
    public String index(final Model model, final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        model.addAttribute("page", "index");
        model.addAttribute("user", new User(oAuth2AuthenticationToken));
        model.addAttribute("bot", bot);
        model.addAttribute("twitchId", getTwitchId(bot));
        return "index";
    }

    @GetMapping("/{home}")
    public String showHome(final Model model, @PathVariable("home") final int homeId,
                           final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        model.addAttribute("user", new User(oAuth2AuthenticationToken));
        model.addAttribute("page", "home");
        BotHome botHome = bot.getHome(homeId);
        model.addAttribute("botHome", botHome);
        model.addAttribute("twitchId", getTwitchId(bot));
        return "bot_home";
    }

    private String getTwitchId(final Bot bot) {
        return ((TwitchService)bot.getService(TwitchService.TYPE)).getClientId();
    }
}
