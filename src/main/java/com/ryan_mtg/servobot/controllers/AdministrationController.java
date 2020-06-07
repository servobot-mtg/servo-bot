package com.ryan_mtg.servobot.controllers;

import com.google.common.collect.Lists;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.controllers.error.ResourceNotFoundException;
import com.ryan_mtg.servobot.data.factories.LoggedMessageSerializer;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Bot;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.user.UserTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller()
@RequestMapping("/admin")
public class AdministrationController {
    private final BotRegistrar botRegistrar;
    private final SuggestionRepository suggestionRepository;
    private final LoggedMessageSerializer loggedMessageSerializer;
    private final UserTable userTable;
    private final Runnable adminTask;

    public AdministrationController(final BotRegistrar botRegistrar, final SuggestionRepository suggestionRepository,
            final UserTable userTable, final LoggedMessageSerializer loggedMessageSerializer,
            @Autowired(required = false) @Qualifier("adminTask") final Runnable adminTask) {
        this.botRegistrar = botRegistrar;
        this.suggestionRepository = suggestionRepository;
        this.userTable = userTable;
        this.loggedMessageSerializer = loggedMessageSerializer;
        this.adminTask = adminTask;
    }

    @GetMapping
    public String admin(final Model model) throws BotErrorException {
        model.addAttribute("bots", botRegistrar.getBots());
        model.addAttribute("page", "admin");
        addUsers(model);
        model.addAttribute("suggestions", suggestionRepository.findAllByOrderByCountDescAliasAsc());
        return "admin/admin";
    }

    @GetMapping("/bot/{bot}/hub")
    public String showBot(final Model model, @PathVariable("bot") final String botName) throws BotErrorException {
        Bot bot = botRegistrar.getBot(botName);
        model.addAttribute("page", "bot");

        if (bot == null) {
            throw new ResourceNotFoundException(String.format("No bot with name %s", botName));
        }

        addBot(model, bot);
        return "admin/bot";
    }

    @GetMapping("/bot/{bot}/book/{book}")
    public String showBook(final Model model, @PathVariable("bot") final String botName,
                           @PathVariable("book") final String bookName) {
        model.addAttribute("page", "book");

        Bot bot = botRegistrar.getBot(botName);
        if (bot == null) {
            throw new ResourceNotFoundException(String.format("No bot with name %s", botName));
        }
        model.addAttribute("contextId", bot.getContextId());

        Optional<Book> book = bot.getBookTable().getBook(bookName);
        if (!book.isPresent()) {
            throw new ResourceNotFoundException(String.format("No book home with name %s", bookName));
        }
        model.addAttribute("book", book.get());

        return "book";
    }


    @GetMapping("/users")
    public String users(final Model model) throws BotErrorException {
        model.addAttribute("page", "users");
        addUsers(model);
        return "admin/users";
    }

    @GetMapping("/messages")
    public String messages(final Model model) throws BotErrorException {
        model.addAttribute("page", "messages");
        model.addAttribute("bot", botRegistrar.getDefaultBot());
        model.addAttribute("messages", loggedMessageSerializer.getLoggedMessages());
        addUsers(model);
        return "admin/messages";
    }

    @PostMapping("/run")
    public String run() {
        if (adminTask != null) {
            adminTask.run();
            return "redirect:/admin";
        } else {
            return "redirect:/admin";
        }
    }

    private void addUsers(final Model model) throws BotErrorException {
        model.addAttribute("users", userTable.getAllUsers());
    }

    private void addBot(final Model model, final Bot bot) {
        model.addAttribute("bot", bot);
        model.addAttribute("commandDescriptors",
                CommandDescriptor.getCommandDescriptors(bot.getCommandTable().getCommandMapping()));
        model.addAttribute("userTable", userTable);
    }

    @ModelAttribute
    private void addMemory(final Model model) {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        model.addAttribute("total_memory", formatMemory(totalMemory));
        model.addAttribute("free_memory", formatMemory(freeMemory));
        model.addAttribute("used_memory", formatMemory(totalMemory-freeMemory));
    }

    @ModelAttribute
    private void addAttributes(final Model model) {
        model.addAttribute("permissions", Lists.newArrayList(
                Permission.ADMIN, Permission.ANYONE));
        model.addAttribute("events", Lists.newArrayList());
    }

    private String formatMemory(final long amount) {
        if (amount > 1024 * 1024) {
            return String.format("%.2f MB", amount/1024./1024);
        }

        if (amount > 1024) {
            return String.format("%.2f KB", amount/1024.);
        }

        return amount + " B";
    }
}
