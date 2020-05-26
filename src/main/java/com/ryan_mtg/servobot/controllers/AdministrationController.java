package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.data.factories.LoggedMessageSerializer;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.user.UserTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @ModelAttribute
    private void addMemory(final Model model) {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        model.addAttribute("total_memory", formatMemory(totalMemory));
        model.addAttribute("free_memory", formatMemory(freeMemory));
        model.addAttribute("used_memory", formatMemory(totalMemory-freeMemory));
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
