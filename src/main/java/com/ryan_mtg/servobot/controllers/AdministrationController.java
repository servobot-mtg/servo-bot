package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AdministrationController {
    @Autowired
    private UserSerializer userSerializer;

    @GetMapping("/admin")
    public String index(final Model model) {
        model.addAttribute("page", "admin");
        model.addAttribute("users", userSerializer.getAllUsers());
        return "admin";
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
