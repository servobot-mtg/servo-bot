package com.ryan_mtg.servobot.controllers;

import com.google.common.collect.Lists;
import com.ryan_mtg.servobot.commands.Permission;
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

import java.util.ArrayList;
import java.util.List;

@Controller
public class BotController {
    @Autowired
    @Qualifier("bot")
    private Bot bot;

    private List<TimeZoneDescriptor> timeZones = new ArrayList<>();

    public BotController() {
        timeZones.add(new TimeZoneDescriptor("America/New_York", "Eastern"));
        timeZones.add(new TimeZoneDescriptor("America/Chicago", "Central"));
        timeZones.add(new TimeZoneDescriptor("America/Denver", "Mountain"));
        timeZones.add(new TimeZoneDescriptor("America/Vancouver", "Pacific"));
    }

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
        model.addAttribute("timeZones", timeZones);
        model.addAttribute("permissions", Lists.newArrayList(
                Permission.ADMIN, Permission.STREAMER, Permission.MOD, Permission.SUB, Permission.ANYONE));
        return "bot_home";
    }

    @ModelAttribute
    private void addBot(final Model model) {
        model.addAttribute("bot", bot);
    }

    public static class TimeZoneDescriptor {
        private String value;
        private String display;

        public TimeZoneDescriptor(final String value, final String display)  {
            this.value = value;
            this.display = display;
        }

        public String getValue() {
            return value;
        }

        public String getDisplay() {
            return display;
        }
    }
}
