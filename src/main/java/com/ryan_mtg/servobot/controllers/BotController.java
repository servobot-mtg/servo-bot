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
    public String showHome(final Model model, @PathVariable("home")  final String homeName) {
        BotHome home = bot.getHome(homeName);
        model.addAttribute("commands", home.getCommandTable().getCommandList());
        model.addAttribute("reactions", home.getReactionTable());
        return "bot_home";
    }
}
