package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class AdministrationController {
    @Autowired
    private UserSerializer userSerializer;

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    @Qualifier("adminTask")
    private Optional<Runnable> adminTask;

    @GetMapping("/admin")
    public String index(final Model model) throws BotErrorException {
        model.addAttribute("page", "admin");
        model.addAttribute("users", userSerializer.getAllUsers());
        model.addAttribute("suggestions", suggestionRepository.findAllByOrderByCountDescAliasAsc());
        return "admin";
    }

    @PostMapping("/admin/run")
    public String run() {
        if (adminTask.isPresent()) {
            adminTask.get().run();
            return "redirect:/admin";
        } else {
            return "redirect:/admin";
        }
    }

    @ModelAttribute
    public void addMemory(final Model model) {
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
