package com.ryan_mtg.servobot.controllers;

import com.google.common.collect.Lists;
import com.ryan_mtg.servobot.commands.trigger.CommandEvent;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.controllers.error.ResourceNotFoundException;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.model.game_queue.Game;
import com.ryan_mtg.servobot.security.WebsiteUser;
import com.ryan_mtg.servobot.utility.TimeZoneDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class BotController {
    private final BotRegistrar botRegistrar;
    private final SerializerContainer serializers;
    private final List<TimeZoneDescriptor> timeZones;

    public BotController(final BotRegistrar botRegistrar, final SerializerContainer serializers) {
        this.botRegistrar = botRegistrar;
        this.serializers = serializers;

        timeZones = TimeZoneDescriptor.TIME_ZONES;
    }

    @GetMapping({"/", "/index.html"})
    public String index(final Model model) {
        model.addAttribute("page", "index");
        return "index";
    }

    @GetMapping("/home")
    public String manage(final Model model) {
        WebsiteUser websiteUser = (WebsiteUser) model.asMap().get("user");
        if (!websiteUser.isAuthenticated() ) {
            model.addAttribute("page", "homeless");
            return "home/homeless";
        }

        if (websiteUser.hasInvite()) {
            model.addAttribute("page", "invite");
            model.addAttribute("botName", botRegistrar.getDefaultBot().getName());
            model.addAttribute("timeZones", timeZones);
            return "home/invite";
        }

        if (websiteUser.isAStreamer()) {
            model.addAttribute("page", "control");
            BotHome botHome = botRegistrar.getBotHome(websiteUser.getBotHomeId());
            model.addAttribute("botHome", botHome);
            model.addAttribute("timeZones", timeZones);
            return "home/control";
        }

        model.addAttribute("page", "wandering");
        return "home/wandering";
    }

    @GetMapping("/help")
    public String showHelp(final Model model) {
        model.addAttribute("page", "help");
        return "help/overview";
    }

    @GetMapping("/help/{page}")
    public String showHelpPage(final Model model, @PathVariable("page") final String page) {
        // TODO: use a map of allowed pages
        model.addAttribute("page", page);
        return String.format("help/%s", page);
    }

    @GetMapping("/home/{home}")
    public String showHome(final Model model, @PathVariable("home") final String homeName) {
        return showHome(model, getBotHome(homeName));
    }

    @GetMapping("/home/{bot}/{home}")
    public String showHome(final Model model, @PathVariable("bot") final String botName,
                           @PathVariable("home") final String homeName) {
        return showHome(model, getBotHome(botName, homeName));
    }

    @GetMapping("/home/{home}/settings")
    public String showHomeSettings(final Model model, @PathVariable("home") final String homeName) {
        return showHomeSettings(model, getBotHome(homeName));
    }

    @GetMapping("/home/{bot}/{home}/settings")
    public String showHomeSettings(final Model model, @PathVariable("bot") final String botName,
                                   @PathVariable("home") final String homeName) {
        return showHomeSettings(model, getBotHome(botName, homeName));
    }

    @GetMapping("/home/{home}/hub")
    public String showHub(final Model model, @PathVariable("home") final String homeName) {
        return showHub(model, getBotHome(homeName));
    }

    @GetMapping("/home/{bot}/{home}/hub")
    public String showHub(final Model model, @PathVariable("bot") final String botName,
                          @PathVariable("home") final String homeName) {
        return showHub(model, getBotHome(botName, homeName));
    }

    @GetMapping("/home/{home}/users")
    public String showUsers(final Model model, @PathVariable("home") final String homeName) {
        return showUsers(model, getBotHome(homeName));
    }

    @GetMapping("/home/{bot}/{home}/users")
    public String showUsers(final Model model, @PathVariable("bot") final String botName,
                            @PathVariable("home") final String homeName) {
        return showUsers(model, getBotHome(botName, homeName));
    }

    @GetMapping("/home/{home}/giveaways")
    public String showGiveaways(final Model model, @PathVariable("home") final String homeName) {
        return showGiveaways(model, getBotHome(homeName));
    }

    @GetMapping("/home/{bot}/{home}/giveaways")
    public String showGiveaways(final Model model, @PathVariable("bot") final String botName,
                                @PathVariable("home") final String homeName) {
        return showGiveaways(model, getBotHome(botName, homeName));
    }


    @GetMapping("/home/{home}/book/{book}")
    public String showBook(final Model model, @PathVariable("home") final String homeName,
                           @PathVariable("book") final String bookName) {
        return showBook(model, getBotHome(homeName), bookName);
    }

    @GetMapping("/home/{bot}/{home}/book/{book}")
    public String showBook(final Model model, @PathVariable("bot") final String botName,
                           @PathVariable("home") final String homeName, @PathVariable("book") final String bookName) {
        return showBook(model, getBotHome(botName, homeName), bookName);
    }

    private BotHome getBotHome(final String homeName) {
        BotHome botHome = botRegistrar.getBotHome(homeName);

        if (botHome == null) {
            throw new ResourceNotFoundException("No bot home with name %s", homeName);
        }

        return botHome;
    }

    private BotHome getBotHome(final String botName, final String homeName) {
        BotHome botHome = botRegistrar.getBotHome(botName, homeName);

        if (botHome == null) {
            throw new ResourceNotFoundException("No bot home with bot name %s and home name ", botName, homeName);
        }

        return botHome;
    }

    private String showHome(final Model model, final BotHome botHome) {
        model.addAttribute("page", "home");

        addBotHome(model, botHome);
        model.addAttribute("users", botHome.getHomedUserTable().getModerators());
        return "bot_home";
    }

    public String showHomeSettings(final Model model, final BotHome botHome) {
        model.addAttribute("page", "settings");
        addBotHome(model, botHome);
        model.addAttribute("timeZones", timeZones);
        return "bot_home_settings";
    }

    public String showHub(final Model model, final BotHome botHome) {
        model.addAttribute("page", "hub");

        if (!isPrivledged(model, botHome)) {
            return String.format("redirect:/home/%s/%s", botHome.getBot().getName(), botHome.getName());
        }

        addBotHome(model, botHome);
        model.addAttribute("timeZones", timeZones);
        return "bot_home_hub";
    }

    public String showUsers(final Model model, final BotHome botHome) {
        if (!isPrivledged(model, botHome)) {
            return String.format("redirect:/home/%s/%s", botHome.getBot().getName(), botHome.getName());
        }

        model.addAttribute("page", "users");

        addBotHome(model, botHome);
        model.addAttribute("users", botHome.getHomedUserTable().getHomedUsers());
        return "users";
    }

    public String showGiveaways(final Model model, final BotHome botHome) {
        if (!isPrivledged(model, botHome)) {
            return String.format("redirect:/home/%s/%s", botHome.getBot().getName(), botHome.getName());
        }

        addBotHome(model, botHome);
        return "giveaways";
    }

    public String showBook(final Model model, final BotHome botHome, @PathVariable("book") final String bookName) {
        model.addAttribute("page", "book");
        model.addAttribute("contextId", botHome.getContextId());

        Optional<Book> book = botHome.getBookTable().getBook(bookName);
        if (!book.isPresent()) {
            throw new ResourceNotFoundException(String.format("No book home with name %s", bookName));
        }
        model.addAttribute("book", book.get());

        return "book";
    }


    @ModelAttribute
    private void addAttributes(final Model model) {
        model.addAttribute("bots", botRegistrar.getBots());
        model.addAttribute("permissions", Lists.newArrayList(
                Permission.ADMIN, Permission.STREAMER, Permission.MOD, Permission.SUB, Permission.ANYONE));
        model.addAttribute("events", Lists.newArrayList(
                CommandEvent.Type.STREAM_START, CommandEvent.Type.SUBSCRIBE, CommandEvent.Type.RAID,
                CommandEvent.Type.NEW_USER));
    }

    private void addBotHome(final Model model, final BotHome botHome) {
        model.addAttribute("botHome", botHome);
        model.addAttribute("commandDescriptors",
                CommandDescriptor.getCommandDescriptors(botHome.getCommandTable().getCommandMapping()));
        model.addAttribute("userTable", serializers.getUserTable());
        model.addAttribute("gameTypes", Arrays.asList(Game.ARENA, Game.AMONG_US));

        ServiceHome serviceHome = botHome.getServiceHome(DiscordService.TYPE);
        if (serviceHome != null) {
            model.addAttribute("emotes", serviceHome.getEmotes());
            model.addAttribute("roles", serviceHome.getRoles());
            model.addAttribute("channels", serviceHome.getChannels());
        } else {
            model.addAttribute("emotes", Lists.newArrayList());
            model.addAttribute("roles", Lists.newArrayList());
            model.addAttribute("channels", Lists.newArrayList());
        }
    }

    private boolean isPrivledged(final Model model, final BotHome botHome) {
        WebsiteUser websiteUser = (WebsiteUser) model.asMap().get("user");
        return websiteUser.isPrivileged(botHome);
    }
}