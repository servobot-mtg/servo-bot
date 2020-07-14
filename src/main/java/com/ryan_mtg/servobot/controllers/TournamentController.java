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
    public String showTournaments(final Model model) {
        model.addAttribute("tournaments", tournamentManager.getTournaments());
        return "tournament/home";
    }

    @GetMapping("/cfb/{name}")
    public String showCfbTournament(final Model model, @PathVariable("name") final String name) {
        try {
            int id = Integer.parseInt(name);
            model.addAttribute("tournament", tournamentManager.getCfbTournament(id));
        } catch (Exception e) {
            model.addAttribute("tournament", tournamentManager.getTournament(name.replace('+', ' ')));
        }
        return "tournament/tournament";
    }

    @GetMapping("/scg/{name}")
    public String showScgTournament(final Model model, @PathVariable("name") final String name) {
        try {
            int id = Integer.parseInt(name);
            model.addAttribute("tournament", tournamentManager.getScgTournament(id));
        } catch (Exception e) {
            model.addAttribute("tournament", tournamentManager.getTournament(name.replace('+', ' ')));
        }
        return "tournament/tournament";
    }
}
