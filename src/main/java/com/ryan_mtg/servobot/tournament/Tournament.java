package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.utility.Time;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
    private String url;

    @Getter @Setter
    private int round;

    @Getter @Setter
    private String pairingsUrl;

    @Getter @Setter
    private String standingsUrl;

    @Getter @Setter
    private String decklistUrl;

    @Getter @Setter
    private PlayerSet playerSet;

    @Getter @Setter
    private Standings standings;

    @Getter
    private Map<Integer, Pairings> pairingsMap = new HashMap<>();

    @Getter @Setter
    private Instant startTime;

    @Getter @Setter
    private TournamentType type;

    @Getter @Setter
    private List<String> formats;

    private Informer informer;

    @Getter @Setter
    private DecklistMap decklistMap;
    private int id;

    public Tournament(final Informer informer, final String name, final int id) {
        this.informer = informer;
        this.name = name;
        this.id = id;
    }

    public void setPairings(final Pairings pairings) {
        pairingsMap.put(pairings.getRound(), pairings);
    }

    private static final List<String> BOUNTIES = Arrays.asList();

    private static final Set<Player> ICONS = new HashSet<>(Arrays.asList(
            Player.createFromName("Kaya Gregg", "🍜")
    ));

    private static final Set<Player> CARE_ABOUTS = new HashSet<>(Arrays.asList(
            Player.createFromName("Carolyn Kavanagh", "🦌"),
            Player.createFromName("Andrew Baeckstrom"),
            Player.createFromName("Cedric Phillips"),
            Player.createFromName("Dylan Donegan"),
            Player.createFromName("Michael Bonde"),
            Player.createFromName("Simon Nielsen"),
            Player.createFromName("Ben Weitz"),
            Player.createFromName("Nicholas Price"),

            Player.createFromName("Brian Braun-Duin", Player.League.MPL),
            Player.createFromName("Autumn Burchett", Player.League.MPL),
            Player.createFromName("Marcio Carvalho", Player.League.MPL),
            Player.createFromName("Andrew Cuneo", Player.League.MPL),
            Player.createFromName("Paulo Vitor Damo da Rosa", "🎖️", Player.League.MPL),
            Player.createFromName("Jean-Emmanuel Depraz", Player.League.MPL),
            Player.createFromName("Javier Dominguez", Player.League.MPL),
            Player.createFromName("Reid Duke", "🎖️", Player.League.MPL),
            Player.createFromName("Piotr Glogowski", Player.League.MPL),
            Player.createFromName("William Jensen", "🎖️", Player.League.MPL),
            Player.createFromName("Martin Juza", "🎖️", Player.League.MPL),
            Player.createFromName("Chris Kvartek", Player.League.MPL),
            Player.createFromName("Raphael Levy", Player.League.MPL),
            Player.createFromName("Seth Manfield", "🎖️", Player.League.MPL),
            Player.createFromName("Andrea Mengucci", Player.League.MPL),
            Player.createFromName("Gabriel Nassif", "🎖️", Player.League.MPL),
            Player.createFromName("Brad Nelson", Player.League.MPL),
            Player.createFromName("Carlos Romao", Player.League.MPL),
            Player.createFromName("Rei Sato", Player.League.MPL),
            Player.createFromName("Lee Shi Tian", "🎖️", Player.League.MPL),
            Player.createFromName("Shahar Shenhar", Player.League.MPL),
            Player.createFromName("Ondrej Strasky", "🍯", Player.League.MPL),
            Player.createFromName("Shota Yasooka", "🎖️", Player.League.MPL),
            Player.createFromName("Ken Yukuhiro", Player.League.MPL),

            Player.createFromName("Matthieu Avignon", Player.League.RIVALS),
            Player.createFromName("Frederico Bastos", Player.League.RIVALS),
            Player.createFromName("Chris Botelho", "😺", Player.League.RIVALS),
            Player.createFromName("Kai Budde", "🎖️", Player.League.RIVALS),
            Player.createFromName("Corey Burkhart", Player.League.RIVALS),
            Player.createFromName("Austin Bursavich", Player.League.RIVALS),
            Player.createFromName("Stanislav Cifka", Player.League.RIVALS),
            Player.createFromName("Louis-Samuel Deltour", Player.League.RIVALS),
            Player.createFromName("Kenji Egashira", Player.League.RIVALS),
            Player.createFromName("Lucas Esper", Player.League.RIVALS),
            Player.createFromName("Jess Estephan", Player.League.RIVALS),
            Player.createFromName("Ivan Floch", Player.League.RIVALS),
            Player.createFromName("Ryuzo Fujie", Player.League.RIVALS),
            Player.createFromName("Simon Görtzen", Player.League.RIVALS),
            Player.createFromName("Beatriz Grancha", Player.League.RIVALS),
            Player.createFromName("Emma Handy", Player.League.RIVALS),
            Player.createFromName("Kenta Harane", Player.League.RIVALS),
            Player.createFromName("Christian Hauck", Player.League.RIVALS),
            Player.createFromName("Alexander Hayne", Player.League.RIVALS),
            Player.createFromName("Yoshihiko Ikawa", Player.League.RIVALS),
            Player.createFromName("Shintaro Ishimura", Player.League.RIVALS),
            Player.createFromName("Eli Kassis", Player.League.RIVALS),
            Player.createFromName("Zachary Kiihne", Player.League.RIVALS),
            Player.createFromName("Grzegorz Kowalski", Player.League.RIVALS),
            Player.createFromName("Riku Kumagai", Player.League.RIVALS),
            Player.createFromName("Joel Larsson", Player.League.RIVALS),
            Player.createFromName("Matias Leveratto", Player.League.RIVALS),
            Player.createFromName("Eli Loveman", Player.League.RIVALS),
            Player.createFromName("Noah Ma", Player.League.RIVALS),
            Player.createFromName("Luca Magni", Player.League.RIVALS),
            Player.createFromName("Theo Moutier", Player.League.RIVALS),
            Player.createFromName("Matt Nass", Player.League.RIVALS),
            Player.createFromName("Gregory Orange", "🍊", Player.League.RIVALS),
            Player.createFromName("Sebastián Pozzo", Player.League.RIVALS),
            Player.createFromName("John Rolf", Player.League.RIVALS),
            Player.createFromName("Luis Salvatto", Player.League.RIVALS),
            Player.createFromName("Bernardo Santos", Player.League.RIVALS),
            Player.createFromName("Luis Scott-Vargas", "🌯", Player.League.RIVALS),
            Player.createFromName("Thoralf Severin", Player.League.RIVALS),
            Player.createFromName("Mike Sigrist", "💖", Player.League.RIVALS),
            Player.createFromName("Miguel Simoes", Player.League.RIVALS),
            Player.createFromName("Matt Sperling", Player.League.RIVALS),
            Player.createFromName("Ben Stark", "🎖️", Player.League.RIVALS),
            Player.createFromName("Yuta Takahashi", Player.League.RIVALS),
            Player.createFromName("Jakub Toth", Player.League.RIVALS),
            Player.createFromName("Ally Warfield", "🌮", Player.League.RIVALS),
            Player.createFromName("Jacob Wilson", Player.League.RIVALS),
            Player.createFromName("Brent Vos", Player.League.RIVALS)
        /*
        new Player( "AliasV#76549", null, null, "AliasV", null, null),
        new Player("DanaFischerMTG#46891", null, "Dana Fischer", null, null, null),
        new Player("GabySpartz#39839", null, "Gaby Spartz", null, "GabySpartz", "GabySpartz"),
        new Player("Em_TeeGee#43111", null, "Emma Handy", null, null, null),
        new Player("rileyquarytower#97553", null, "Riley Knight", null, null, null),
        new Player("JANYAN#43274", null, "Jana Amari", null, null, null),
        new Player("Shieldmaiden#51443", null, null, "Ashlizzlle", null, null),
        new Player("Yellowhat#43550", null, "Gabriel Nassif", null, null, null),

        new Player(null, null, "Alexander Hayne", "Alexander Hayne", null, "InsayneHayne"),
        new Player(null, null, "Ally Warfield", "Meebo", "mythic_meebo", "MythicMeebo"),
        new Player(null, null, "Andrea Mengucci", "Mengu", "AndreaMengucci", "Mengu09"),
        new Player(null, null, "Autumn Burchett", null, "autumnlilymtg", "AutumnLilyMTG"),
        new Player(null, null, "Benjamin Weitz", "Ben Weitz", null, "bsweitz123"),
        new Player(null, null, "Brian Braun-Duin", "BBD", null, "NotDuinIt"),
        new Player(null, null, "Chris Kvartek", null, "kavartech", "Kavartech"),
        new Player(null, null, "Eli Kassis", null, "elikassis", "Eli_Kassis"),
        new Player(null, null, "Eduardo Sajgalik", null, "walaoumpa", "Walaoumpa"),
        new Player(null, null, "kenta harane", "Kenta Harane", "jspd_", "jspd_"),
        new Player(null, null, "Brad Nelson", null, "FFfreakmtg", "fffreakmtg"),
        new Player(null, null, "shi tian lee", "Lee Shi Tian", "leearson", "leearson"),
        new Player(null, null, "Abe Corrigan", null, null, "CorriganAbe"),
        new Player(null, null, "Corey Burkhart", null, null, "Corey_Burkhart"),
        new Player(null, null, "Martin Juza", null, "martinjuza", "MartinJuza"),
        new Player(null, null, "Ivan Floch", null, null, "IvanFloch_"),
        new Player(null, null, "Jean-Emmanuel Depraz", null, null, null),
        new Player(null, null, "Simon Görtzen", null, null, "simongoertzen"),
        new Player(null, null, "Javier Dominguez", null, "JavierDmagic", "JavierDmagic"),
        new Player(null, null, "Joel Larsson", null, null, "JoelLarssonGG"),
        new Player(null, null, "kanister", "Piotr Glogowski", "kanister_mtg", "kanister_mtg"),
        new Player(null, null, "ken yukuhiro", "Ken Yukuhiro", null, "death_snow"),
        new Player(null, null, "Marcio Carvalho", null, null, "KbolMagic"),
        new Player(null, null, "benjamin stark", "BenS", "null", "BenS_MTG"),
        new Player(null, null, "Andrew Cuneo", null, null, "AndrewCuneo"),
        new Player(null, null, "Mike Sigrist", "Siggy", "msigrist83", "MSigrist83"),
        new Player(null, null, "Oliver Tomajko", null, null, "OliverTomajko"),
        new Player(null, null, "Ondrej Strasky", null, "OndrejStrasky", "OndrejStrasky"),
        new Player(null, null, "Martin Müller", null, null, "Mullermtg"),
        new Player(null, null, "paulo vitor damo da rosa", "PVDDR", null, "PVDDR"),
        new Player(null, null, "Logan Nettles", null, "jaberwocki", "jaberwocki"),
        new Player(null, null, "Carlos Romao", null, null, "Jabsmtg"),
        new Player(null, null, "Raphael Levy", null, null, "raphlevymtg"),
        new Player(null, null, "Gabriel Nassif", null, "yellowhat", "yellowhat"),
        new Player(null, null, "Rei Sato", null, null, "r_0310"),
        new Player(null, null, "Reid Duke", null, "reiderrabbit", "ReidDuke"),
        new Player(null, null, "Andrew Baeckstrom", "BK", "abaeckstrom", "abaeckst"),
        new Player(null, null, "SethManfieldMTG", "Seth Manfield", "sethmanfieldmtg", "SethManfield"),
        new Player(null, null, "Shahar Shenhar", null, "shahar_shenhar", "shaharshenhar"),
        new Player(null, null, "Sebastián Pozzo", null, null, "sebastianpozzo"),
        new Player(null, null, "Smdster", "Sam Pardee", "Smdster", "Smdster"),
        new Player(null, null, "Thiago Saporito", null, null, "bolov0"),
        new Player(null, null, "Thoralf Severin", null, null, "ToffelMTG"),
        new Player(null, null, "yaya3", "Shota Yasooka", "", "yaya3_"),
        new Player(null, null, "William Jensen", "Huey", "hueywj", "HueyJensen"),
        new Player(null, null, "YUTA TAKAHASHI", "Yuta Takahashi", "vendilion_mtg", "Vendilion")
            //new Player(null, null, "", null, null, null),

        /*
        new Player("MZBlazer#72009", null, null, null, null, "MTGMilan"),
        new Player("Filipa#15754", null, null, null, "filipacarola", "filipamtg"),
        new Player("Booradley95#84650", null, null, null, null, "bradleyyoo_mtg"),
        new Player("Conanhawk#53621", null, "Eric Hawkins", null, "conanhawk", "conanhawk"),
        new Player("h0lydiva#65001", null, "Daniela Diaz", null, "h0lydiva", "h0lyDiva"),
        new Player("themightylinguine#94385", null, "Carolyn Kavanagh", "Moosers",
                "themightylinguine", "mightylinguine"),

        new Player("Ben_Stark#20548", null, "Ben Stark", "BenS", "bens_mtg", "BenS_MTG"),
        new Player("Brad_Nelson#99373", null, "Brad Nelson", "Bard Nelson", "FFfreakmtg", "fffreakmtg"),
        new Player("Dylan_Nollen#24794", null, "Dylan Nollen", "Ni_Hao_DyLan", "ni_hao_Dylan", "Ni_Hao_DyLan"),
        new Player("Eric_Froehlich#19362", null, "Eric Froehlich", "EFro", "efropoker", "efropoker"),
        new Player("Harlan_Firer#24130", null, "Harlan Firer", "Harlan Firer", null, "HarlanFirer"),
        new Player("Ben_Honaker#85200", null, "Ben Honaker", "Ben Honaker", "imissedmyq", "IMissedMyQ"),
        new Player("Jason_Fleurant#38295", null, "Jason Fleurant", "Jason Fleurant", "jasonfleurant", "JasonFleurant"),
        new Player("Jeremy_Dezani#79986", null, "Jeremy Dezani", "Jeremy Dezani", "jeremdez", "JDezani"),
        new Player("Jessica_Estephan#45539", null, "Jessica Estephan", null, "jesstephan", "jesstephan"),
        new Player("Gal_Schlesinger#42277", null, "Gal Schlesinger", "Yamakiller", "yamakiller", "yamakiller_MTG"),
        new Player("Giana_Kaplan#05304", null, "Giana Kaplan", "Bloody", "Bloody", "Bloody"),
        new Player("Logan_Nettles#78747", null, "Logan Nettles", "Jaberwocki", "jaberwocki", "Jaberwocki"),
        new Player("Marcela_Almeida#18118", null, "Marcela Almeida", null, null, "LindaMahzinha"),
        new Player("Seth_Manfield#52341", null, "Seth Manfield", "Seth Manfield", "sethmanfieldmtg" , "SethManfield"),
        new Player("ShiTian_Lee#26042", null, "Lee Shi Tian", "Lee Shi Tian", "leearson", "leearson"),
        new Player("Teruya_Kakumae#36412", null, "Teruya Kakumae", "Teruya Kakumae", null,"fushiginokunin5"),
        new Player("Tom_Ross#34256", null, "Tom Ross", "Tom 'the Boss' Ross", "BossMTG", "Boss_MTG"),
        new Player("Yoshihiko_Ikawa#15013", null, "Yoshihiko Ikawa", "Yoshihiko Ikawa", "WanderingOnes_", "WanderingOnes"),

        new Player("Aaron_Barich#54175", null, "Aaron Barich", "Aaron_Barich", "RuneclawBarich", "RuneclawBarich"),
        new Player("Alexander_Hayne#41179", null, "Alexander_Hayne", "Insayne Hayne", "insaynehayne", "InsayneHayne"),
        new Player("Allen_Wu#39459", null, "Allen Wu", "Allen Wu", null, "nalkpas"),
        new Player("Andrea_Mengucci#27316", null, "Andrea Mengucci", "Mengu", "andreamengucci", "Mengu09"),
        new Player("Ashley_MunozPreyeses#64645", null, "Ashley Munoz Preyeses", "Ashlizzlle", "ashlizzlle", "F2K_Ashlizzlle"),
        new Player("Carlos_Romao#28636", null, "Carlos Romao", "Carlos Romao", "cadu_romao", "Jabsmtg"),
        new Player("Christopher_Kvartek#57153", null, "Christopher Kvartek", "Chris Kvartek", "bavartech", "Kavartech"),
        new Player("Corey_Burkhart#92780", null, "Corey Burkhart", "Corey Burkhart", null, "Corey_Burkhart"),
        new Player("Dylan_Donegan#98054", null, "Dylan Donegan", "Dylan Donegan", null, "DylanD_MTG"),
        new Player("Eric_Severson#37783", null, "Eric Severson", null, null, "EricESeverson"),
        new Player("Gabriel_Nassif#64992", null, "Gabriel_Nassif", "Yellow Hat", "yellowhat", "gabnassif"),
        new Player("Gerard_Fabiano#41670", null, "Gerard Fabiano", null, null, null),
        new Player("Jack_Kiefer#39736", null, "Jack Kiefer", "Jack Kiefer", null, "JackBKiefer"),
        new Player("Jelger_Wiegersma#50276", null, "Jelger Wiegersma", "Jelger_Wiegersma", null, "WJelger"),
        new Player("John_Rolf#23321", null, "John Rolf", "John Rolf", null, "JRolfMTG"),
        new Player("Kenji_Egashira#32540", null, "Kenji Egashira", "Numot the Nummy", "NumotTheNummy", "NumotTheNummy"),
        new Player("Lucas_EsperBerthoud#73368", null, "Lucas Esper Berthoud", "Lucas Esper Berthoud", "lucas_esper_berthoud", "bertuuuu"),
        new Player("Luis_Salvatto#16501", null, "Luis Salvatto", "LSV (Luis Salvatto)", "luissalvatto", "LuisSalvatto"),
        new Player("Marcio_Carvalho#28667", null, "Marcio Carvalho", "Marcio Carvalho", "kbol_", "KbolMagic"),
        new Player("Mason_Clark#60209", null, "Mason Clark", "Mason Clark", "themasonclark", "masoneclark"),
        new Player("Matias_Leveratto#57297", null, "Matias Leveratto", "Matias Leveratto", "levunga21", "levunga"),
        new Player("Michael_Jacob#11358", null, "Michael_Jacob", "Darkest Mage", "darkest_mage", "Darkest_MAJ"),
        new Player("NoAh_Ma#86867", null, "No Ah Ma", null, null, null),
        new Player("Pascal_Vieren#33243", null, "Pascal Vieren", "Pascal Vieren", null, "VierenPascal"),
        new Player("Patrick_Chapin#86733", null, "Patrick Chapin", "The Innovator", null, "thepchapin"),
        new Player("Peter_Ward#83518", null, "Pete Ward", "Killablastlol", "killablastlol", "Killablastlol"),
        new Player("Reid_Duke#39683", null, "Reid Duke", "Reid Duke", "reiderrabbit", "ReidDuke"),
        new Player("Sebastian_Pozzo#95156", null, "Sebastian Pozzo", "Sebastian Pozzo", "sebastianpozzo", "sebastianpozzo"),
        new Player("Shahar_Shenhar#18991", null, "Shahar Shenhar", "Shahar Shenhar", "shahar_shenhar", "shaharshenhar"),
        new Player("Shota_Yasooka#52724", null, "Shota Yasooka", "Shota Yasooka", "yaya3_", "yaya3_"),
        new Player("Simon_Nielsen#67571", null, "Simon Nielsen", "Simon Nielsen", "redbuttontie", "MrChecklistcard"),
        new Player("Steven_Rubin#44697", null, "Steve Rubin", "Steve Rubin", null, "RubinZoo"),
        new Player("Theo_Moutier#82420", null, "Theo Moutier", null, null, null),
        new Player("Thiago_Saporito#93539", null, "Thiago Saporito", "Thiago Saporito", null, "bolov0"),
        new Player("Thoralf_Severin#83661", null, "Thoralf Severin", "Toffel", null, "ToffelMTG"),
        new Player("Tommi_Hovi#24660", null, "Tommi Hovi", null, null, null),
        new Player("William_Jensen#25008", null, "William Jensen", "Huey", "hueywj", "HueyJensen")
        //new Player("", null, "", null, null, null),
         */
    ));

    public Pairings getMostRecentPairings() {
        if (pairingsMap.isEmpty()) {
            return null;
        }
        int maxPairingsRound = Collections.max(pairingsMap.keySet());
        return pairingsMap.get(maxPairingsRound);
    }

    public List<PlayerStanding> getPlayers() {
        List<PlayerStanding> players = new ArrayList<>();
        mergeCareAbouts(playerSet, CARE_ABOUTS);
        mergeCareAbouts(playerSet, ICONS);

        Record leaderRecord = getLeaderRecord();
        boolean active = isRoundActive();

        for (Player player : playerSet) {
            Pairings mostRecentPairings = getMostRecentPairings();
            String format = mostRecentPairings.getFormat();
            Player opponent = active ? mostRecentPairings.getOpponent(player) : null;
            DecklistDescription opponentDecklist = active ? decklistMap.get(opponent, format) : null;

            int rank = standings != null ? standings.getRank(player) : 0;
            if (rank == Standings.UNKNOWN_RANK) {
                rank = 0;
            }
            Record record = standings != null ? standings.getRecord(player) : Record.newRecord(0, 0);
            boolean dropped = mostRecentPairings.hasDropped(player);
            PlayerStanding.Result result = PlayerStanding.Result.NONE;
            if (active && mostRecentPairings.hasResult(player)) {
                result = mostRecentPairings.getResult(player);
            }
            boolean bounty = BOUNTIES.contains(player.getArenaName());
            players.add(new PlayerStanding(player, rank, isWatchable(player), isLeader(leaderRecord, player), record,
                    result, decklistMap.get(player, format), dropped, opponent, opponentDecklist, bounty));
        }

        Collections.sort(players);
        return players;
    }

    public boolean isRoundActive() {
        int maxPairingsRound = Collections.max(pairingsMap.keySet());
        if (maxPairingsRound == 0) {
            return false;
        }
        return !getMostRecentPairings().isDone();
    }

    public List<ArchetypeDescription> getMetagameBreakdown(final String format) {
        Map<String, Integer> archetypeCount = new HashMap<>();
        int count = 0;
        for (PlayerDecklist decklist : decklistMap.getDecklists(format)) {
            Player player = decklist.getPlayer();
            String decklistName = decklist.getDecklistDescription().getName();
            Record record = standings.getRecord(player);
            int minPoints = (standings.getRound() - 4) * 3;
            if (record.getPoints() >= minPoints) {
                int previousCount = archetypeCount.computeIfAbsent(decklistName, name -> 0);
                archetypeCount.put(decklistName, previousCount + 1);
                count++;
            }
        }

        List<ArchetypeDescription> archetypeDescriptions = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : archetypeCount.entrySet()) {
            archetypeDescriptions.add(new ArchetypeDescription(entry.getKey(), entry.getValue(),
                    (double)entry.getValue() / count));
        }

        Collections.sort(archetypeDescriptions);
        return archetypeDescriptions;
    }

    public boolean hasStarted() {
        if (startTime != null) {
            return Instant.now().compareTo(startTime) >= 0;
        }
        return true;
    }

    public String getTimeUntilStart() {
        return Time.toReadableString(Duration.between(Instant.now(), startTime));
    }

    private void mergeCareAbouts(final PlayerSet playerSet, final Set<Player> careAbouts) {
        for (Player careAboutPlayer : careAbouts) {
            Player foundPlayer = playerSet.find(careAboutPlayer);
            if (foundPlayer != null) {
                playerSet.merge(careAboutPlayer);
            }
        }
    }

    private boolean isLeader(final Record leaderRecord, final Player player) {
        if (leaderRecord == null || standings == null) {
            return false;
        }

        Record playerRecord = standings.getRecord(player);
        return playerRecord.compareTo(leaderRecord) >= 0;
    }

    private boolean isWatchable(final Player player) {
        for (Player careAboutPlayer : CARE_ABOUTS) {
            if (player.getArenaName() != null && player.getArenaName().equals(careAboutPlayer.getArenaName())) {
                return true;
            }

            if (player.getRealName() != null && player.getRealName().equals(careAboutPlayer.getRealName())) {
                return true;
            }
        }
        return false;
    }

    private Record getLeaderRecord() {
        if (standings == null) {
            return Record.newRecord(0, 0);
        }
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
