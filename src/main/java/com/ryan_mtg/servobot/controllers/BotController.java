package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public String index(final Model model) {
        model.addAttribute("bot", bot);
        return "index";
    }

    @GetMapping("/{home}")
    public String showHome(final Model model, @PathVariable("home")  final int homeId) {
        BotHome botHome = bot.getHome(homeId);
        model.addAttribute("botHome", botHome);
        model.addAttribute("commands", botHome.getCommandTable().getCommandList());
        model.addAttribute("reactions", botHome.getReactionTable());
        model.addAttribute("alerts", botHome.getCommandTable().getAlertGenerators());
        return "bot_home";
    }
}
