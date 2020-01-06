package com.ryan_mtg.servobot.controllers;

import com.google.common.collect.Lists;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandMapping;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.Trigger;
import com.ryan_mtg.servobot.controllers.exceptions.ResourceNotFoundException;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.security.WebsiteUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Controller
public class BotController {
    private static Logger LOGGER = LoggerFactory.getLogger(BotController.class);

    @Autowired
    @Qualifier("bot")
    private Bot bot;

    @Autowired
    private SerializerContainer serializers;

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
    public String showHome(final Model model, @PathVariable("home") final String homeName) {
        model.addAttribute("page", "home");
        BotHome botHome = bot.getHome(homeName);
        if (botHome == null) {
            throw new ResourceNotFoundException(String.format("No bot home with name %s", homeName));
        }

        addBotHome(model, botHome);
        model.addAttribute("users", serializers.getUserSerializer().getModerators(botHome.getId()));
        return "bot_home";
    }

    @GetMapping("/home/{home}/hub")
    public String showHub(final Model model, @PathVariable("home") final String homeName) {
        BotHome botHome = bot.getHome(homeName);
        if (botHome == null) {
            throw new ResourceNotFoundException(String.format("No bot home with name %s", homeName));
        }

        if (!isPrivledged(model, botHome)) {
            return String.format("redirect:/home/%s", homeName);
        }

        model.addAttribute("page", "hub");

        addBotHome(model, botHome);
        model.addAttribute("timeZones", timeZones);
        return "bot_home_hub";
    }

    @GetMapping("/home/{home}/users")
    public String showUsers(final Model model, @PathVariable("home") final String homeName) {
        BotHome botHome = bot.getHome(homeName);
        if (botHome == null) {
            throw new ResourceNotFoundException(String.format("No bot home with name %s", homeName));
        }

        if (!isPrivledged(model, botHome)) {
            return String.format("redirect:/home/%s", homeName);
        }

        model.addAttribute("page", "users");

        addBotHome(model, botHome);
        model.addAttribute("users", serializers.getUserSerializer().getHomedUsers(botHome.getId()));
        return "users";
    }


    @GetMapping("/home/{home}/book/{book}")
    public String showBook(final Model model, @PathVariable("home") final String homeName,
                           @PathVariable("book") final String bookName) {
        model.addAttribute("page", "book");

        BotHome botHome = bot.getHome(homeName);
        if (botHome == null) {
            throw new ResourceNotFoundException(String.format("No bot home with name %s", homeName));
        }
        model.addAttribute("botHome", botHome);

        Book book = botHome.getBooks().stream().filter(b -> b.getName().equals(bookName)).findFirst().orElse(null);
        if (book == null) {
            throw new ResourceNotFoundException(String.format("No book home with name %s", bookName));
        }
        model.addAttribute("book", book);

        return "book";
    }

    @ModelAttribute
    private void addBot(final Model model) {
        model.addAttribute("bot", bot);
    }

    private void addBotHome(final Model model, final BotHome botHome) {
        model.addAttribute("botHome", botHome);
        model.addAttribute("commandDescriptors",
                getCommandDescriptors(botHome.getCommandTable().getCommandMapping()));
        model.addAttribute("userSerializer", serializers.getUserSerializer());
        model.addAttribute("permissions", Lists.newArrayList(
                Permission.ADMIN, Permission.STREAMER, Permission.MOD, Permission.SUB, Permission.ANYONE));
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

    private List<CommandDescriptor> getCommandDescriptors(final CommandMapping commandMapping) {
        List<CommandDescriptor> commands = new ArrayList<>();
        Map<Command, CommandDescriptor> commandMap = new HashMap<>();

        Function<Command, CommandDescriptor> createCommandDescriptor = command -> {
            CommandDescriptor newDescriptor = new CommandDescriptor(command);
            commands.add(newDescriptor);
            return newDescriptor;
        };

        for (Map.Entry<Integer, Command> entry : commandMapping.getIdToCommandMap().entrySet()) {
            commandMap.computeIfAbsent(entry.getValue(), createCommandDescriptor);
        }

        for (Map.Entry<Trigger, Command> entry : commandMapping.getTriggerCommandMap().entrySet()) {
            CommandDescriptor descriptor = commandMap.computeIfAbsent(entry.getValue(), createCommandDescriptor);
            descriptor.addTrigger(entry.getKey());
        }

        return commands;
    }

    private boolean isPrivledged(final Model model, final BotHome botHome) {
        WebsiteUser websiteUser = (WebsiteUser) model.asMap().get("user");

        return websiteUser.isAuthenticated() &&
                websiteUser.getRoles().contains(String.format("ROLE_MOD:%d", botHome.getId()));
    }

}
