package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.channelfireball.mfo.MfoInformer;
import com.ryan_mtg.servobot.channelfireball.mfo.model.DecklistDescription;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Pairings;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Player;
import com.ryan_mtg.servobot.channelfireball.mfo.model.PlayerSet;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Record;
import com.ryan_mtg.servobot.channelfireball.mfo.model.RecordCount;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Standings;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tournament {
    private static final int LEADERS = 16;

    @Getter
    private String name;

    @Getter @Setter
    private String nickName;

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

    @Getter @Setter
    private Pairings pairings;

    private MfoInformer mfoInformer;
    private int id;

    public Tournament(final MfoInformer informer, final String name, final int id) {
        this.mfoInformer = informer;
        this.name = name;
        this.id = id;
    }

    private static final Set<String> CARE_ABOUTS = new HashSet<>(Arrays.asList(
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

        "Abe_Corrigan#72641",
        "Adam_Snook#66777",
        "Alex_Majlaton#66191",
        "Allen_Wu#39459",
        "Allison_Warfield#79582",
        "Andrew_Baeckstrom#58108",
        "Andrew_Cuneo#27518",
        "Andrew_Elenbogen#07889",
        "Ben_Wienburg#34295",
        "Ben_Phipps#41960",
        "Benjamin_Weitz#76084",
        "Brandon_Burton#17300",
        "Brandon_Downs#83747",
        "Brian_Braun_Duin#82050",
        "Cedric_Phillips#54002",
        "Chris_Botelho#73594",
        "Christian_Hauck#38502",
        "Corey_Baumeister#75385",
        "Daniel_Jessup#73221",
        "Eduardo_Sajgalik#53023",
        "Eli_Kassis#52723",
        "Eli_Loveman#69476",
        "Emma_Handy#56365",
        "Evan_Whitehouse#69409",
        "Greg_Orange#67819",
        "Gregory_Michel#89100",
        "Jackson_Hicks#18633",
        "Jacob_Wilson#81679",
        "Jarvis_Yu#13799",
        "Jean_Emmanuel_Depraz#55209",
        "Jonathan_Rosum#28137",
        "Josh_Utter_Leyton#25183",
        "Ken_Yukuhiro#53895",
        "Lito_Biala#50544",
        "Lucas_EsperBerthoud#73368",
        "Luis_Scott_Vargas#09399",
        "Mani_Davoudi#06522",
        "Martin_Muller#47270",
        "Matthew_Nass#90250",
        "Matthew_Sperling#18849",
        "Michael_Bonde#26345",
        "Mike_Sigrist#45821",
        "Nathaniel_Knox#47118",
        "Oliver_Tiu#82624",
        "Oliver_Tomajko#80483",
        "Paul_Rietzl#06688",
        "PauloVitor_DamodaRosa#32738",
        "Pierre_Dagen#48314",
        "Piotr_Glogowski#65151",
        "Samuel_Pardee#84546",
        "Willy_Edel#13328",
        "Wyatt_Darby#64959",
        "Zvi_Mowshowitz#00907"
    ));

    public List<PlayerStanding> getPlayersToWatch() {
        List<PlayerStanding> playersToWatch = new ArrayList<>();
        PlayerSet playerSet = standings.getPlayerSet();
        Record leaderRecord = getLeaderRecord();

        Map<Player, DecklistDescription> decklistMap = mfoInformer.parseDecklistsFor(standings.getPlayerSet(), id);

        for (Player player : playerSet) {
            if (isWatchable(leaderRecord, player)) {
                Player opponent = pairings.getOpponent(player);
                playersToWatch.add(new PlayerStanding(player, standings.getRank(player), opponent,
                        standings.getRecord(player), decklistMap.get(player), decklistMap.get(opponent)));
            }
        }

        Collections.sort(playersToWatch);
        return playersToWatch;
    }

    private boolean isWatchable(final Record leaderRecord, final Player player) {
        Record playerRecord = standings.getRecord(player);
        if (leaderRecord != null && playerRecord.compareTo(leaderRecord) >= 0) {
            return true;
        }

        return  CARE_ABOUTS.contains(player.getArenaName());
    }

    private Record getLeaderRecord() {
        List<RecordCount> recordCounts = standings.getRecordCounts(4);
        if (recordCounts.isEmpty() || recordCounts.get(0).getCount() > LEADERS) {
            return null;
        }

        int leaders = 0;
        int index = 0;
        Record bestRecord = recordCounts.get(0).getRecord();
        while (index < recordCounts.size() && leaders + recordCounts.get(index).getCount() <= LEADERS) {
            leaders += recordCounts.get(index).getCount();
            bestRecord = recordCounts.get(index).getRecord();
            index++;
        }

        return bestRecord;
    }
}
