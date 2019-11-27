package com.ryan_mtg.servobot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {
    @GetMapping("/login")
    public String login() {
        return "redirect:/";
    }
}
