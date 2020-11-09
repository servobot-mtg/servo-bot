package com.ryan_mtg.servobot.mpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mpl")
public class MplController {
    @Autowired
    private LeagueManager leagueManager;

    @GetMapping("")
    public String showTournaments(final Model model) {
        model.addAttribute("mpl", leagueManager.getMpl());
        model.addAttribute("mplRounds", leagueManager.getMplRounds());
        model.addAttribute("rivals", leagueManager.getRivals());
        model.addAttribute("league", leagueManager);
        model.addAttribute("schedules", leagueManager.getSchedules());
        return "mpl/home";
    }
}

