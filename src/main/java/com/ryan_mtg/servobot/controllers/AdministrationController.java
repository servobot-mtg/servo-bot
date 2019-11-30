package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
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
    @Qualifier("adminTask")
    private Optional<Runnable> adminTask;

    @GetMapping("/admin")
    public String index(final Model model) {
        model.addAttribute("page", "admin");
        model.addAttribute("users", userSerializer.getAllUsers());
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
        model.addAttribute("total_memory", totalMemory);
        model.addAttribute("free_memory", freeMemory);
        model.addAttribute("used_memory", totalMemory-freeMemory);
    }
}
