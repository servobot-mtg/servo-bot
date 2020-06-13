package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.channelfireball.mfo.model.Player;
import com.ryan_mtg.servobot.channelfireball.mfo.model.PlayerSet;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Standings;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tournament {
    @Getter
    private String name;

    @Getter @Setter
    private int round;

    @Getter @Setter
    private String pairingsUrl;

    @Getter @Setter
    private String standingsUrl;

    @Getter @Setter
    private String decklistUrl;

    @Getter @Setter
    private Standings standings;

    public Tournament(final String name) {
        this.name = name;
    }

    private static final String[] CARE_ABOUTS = {
        "Louis_Samuel_Deltour#09182",
        "Kenta_Harane#51598",
        "Makihito_Mihara#04275",
        "Kenji_Tsumura#57466",
        "Elias_Watsfeldt#18936",
        "Mattia_Rizzi#50511",
        "Simon_Goertzen#50938",
        "Kai_Budde#09796",
        "Shuhei_Nakamura#59144",
        "Dmitriy_Butakov#21736",
        "Martin_Juza#64146",
        "Joel_Larsson#09727",
        "Guillaume_Wafo_Tapa#52597",
        "Yuuki_Ichikawa#18840",
        "Petr_Sochurek#25335",
        "Christian_Calcano#36779",
        "Stanislav_Cifka#50960",
        "Raphael_Levy#80161",
        "Javier_Dominguez#31307",
        "Ivan_Floch#54707",
        "Autumn_Burchett#45139",
        "Rei_Sato#81120",
        "Ondrej_Strasky#99817",
        "Frank_Karsten#00970",
    };

    public List<PlayerStanding> getPlayersToWatch() {
        List<PlayerStanding> playersToWatch = new ArrayList<>();
        PlayerSet playerSet = standings.getPlayerSet();
        for (String arenaName : CARE_ABOUTS) {
            Player player = playerSet.findByArenaName(arenaName);
            if (player != null) {
                playersToWatch.add(new PlayerStanding(player, standings.getRecord(player)));
            }
        }
        Collections.sort(playersToWatch);
        return playersToWatch;
    }
}
