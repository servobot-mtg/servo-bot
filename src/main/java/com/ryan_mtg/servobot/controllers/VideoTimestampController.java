package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.timestamp.VideoTimestampManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manda")
@RequiredArgsConstructor
public class VideoTimestampController {
    public final VideoTimestampManager timestampManager;

    @GetMapping("")
    public String showTournaments(final Model model) {
        model.addAttribute("timestamps", timestampManager.getVideoTimeStamps());
        return "timestamps/index";
    }
}