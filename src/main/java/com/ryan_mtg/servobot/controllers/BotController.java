package com.ryan_mtg.servobot.controllers;

import com.google.common.collect.Lists;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandAlert;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandEvent;
import com.ryan_mtg.servobot.commands.CommandMapping;
import com.ryan_mtg.servobot.commands.HomeCommand;
import com.ryan_mtg.servobot.commands.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.controllers.exceptions.ResourceNotFoundException;
import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.user.HomedUser;
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
    @Autowired
    @Qualifier("bot")
    private Bot bot;

    @Autowired
    private UserSerializer userSerializer;

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

        model.addAttribute("botHome", botHome);
        model.addAttribute("commandDescriptors",
                getCommandDescriptors(botHome.getCommandTable().getCommandMapping()));
        model.addAttribute("timeZones", timeZones);
        model.addAttribute("userSerializer", userSerializer);
        model.addAttribute("users", userSerializer.getHomedUsers(botHome.getId()));
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

    private List<CommandDescriptor> getCommandDescriptors(final CommandMapping commandMapping) {
        List<CommandDescriptor> commands = new ArrayList<>();
        Map<Command, CommandDescriptor> commandMap = new HashMap<>();

        Function<Command, CommandDescriptor> createCommandDescriptor = command -> {
            CommandDescriptor newDescriptor = new CommandDescriptor(command);
            commands.add(newDescriptor);
            return newDescriptor;
        };

        for (Map.Entry<Integer, Command> entry : commandMapping.getIdtoCommandMap().entrySet()) {
            commandMap.computeIfAbsent(entry.getValue(), createCommandDescriptor);
        }

        for (Map.Entry<CommandAlias, MessageCommand> entry : commandMapping.getAliasCommandMap().entrySet()) {
            CommandDescriptor descriptor = commandMap.computeIfAbsent(entry.getValue(), createCommandDescriptor);
            descriptor.addAlias(entry.getKey());
        }

        for (Map.Entry<CommandEvent, Command> entry : commandMapping.getEventCommandMap().entrySet()) {
            CommandDescriptor descriptor = commandMap.computeIfAbsent(entry.getValue(), createCommandDescriptor);
            descriptor.addEvent(entry.getKey());
        }

        for (Map.Entry<CommandAlert, HomeCommand> entry : commandMapping.getAlertCommandMap().entrySet()) {
            CommandDescriptor descriptor = commandMap.computeIfAbsent(entry.getValue(), createCommandDescriptor);
            descriptor.addAlert(entry.getKey());
        }

        return commands;
    }
}
