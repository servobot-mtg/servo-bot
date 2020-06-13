package com.ryan_mtg.servobot.controllers;

import com.ryan_mtg.servobot.tournament.TournamentManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tournament")
public class TournamentController {
    private TournamentManager tournamentManager;

    public TournamentController(final TournamentManager tournamentManager) {
        this.tournamentManager = tournamentManager;
    }

    @GetMapping("")
    public String manage(final Model model) {
        model.addAttribute("tournaments", tournamentManager.getTournaments());
        return "tournament/home";
    }

    @GetMapping("/{name}")
    public String manage(final Model model, @PathVariable("name") final String name) {
        model.addAttribute("tournament", tournamentManager.getTournament(name));
        return "tournament/tournament";
    }
}
