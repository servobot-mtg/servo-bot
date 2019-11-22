package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.controllers.exceptions.ResourceNotFoundException;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BotController {
    @Autowired
    @Qualifier("bot")
    private Bot bot;

    @GetMapping("/")
    public String index(final Model model) {
        model.addAttribute("page", "index");
        return "index";
    }

    @GetMapping("/home/{home}")
    public String showHome(final Model model, @PathVariable("home") final int homeId) {
        model.addAttribute("page", "home");
        BotHome botHome = bot.getHome(homeId);
        if (botHome == null) {
            throw new ResourceNotFoundException(String.format("No bot home with id %d", homeId));
        }
        model.addAttribute("botHome", botHome);
        return "bot_home";
    }

    @ModelAttribute
    private void addBot(final Model model) {
        model.addAttribute("bot", bot);
    }
}
