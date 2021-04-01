package com.ryan_mtg.servobot.mpl;

import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class LeagueManager {
    @Getter
    private final PlayerSet mpl;

    @Getter
    private final PlayerSet rivals;

    private final Map<Integer, Round> mplRounds;

    private final Map<Integer, Round> rivalsRounds;

    @Getter
    private final List<Schedule> schedules;

    public LeagueManager() {
        mpl = loadPlayers("mpl");
        rivals = loadPlayers("rivals");
        mplRounds = loadRounds("mpl", mpl);
        rivalsRounds = loadRounds("rivals", rivals);

        schedules = makeSchedules(mpl, mplRounds);
        schedules.addAll(makeSchedules(rivals, rivalsRounds));
    }

    public List<Round> getMplRounds() {
        return roundize(mplRounds);
    }

    public Round getMplRound(final int number) {
        return mplRounds.get(number);
    }

    public Round getRivalsRound(final int number) {
        return rivalsRounds.get(number);
    }

    private PlayerSet loadPlayers(final String league) {
        Scanner scanner = loadFile(String.format("/mpl/%s.players", league));

        Map<String, Player> byNameMap = new HashMap<>();
        int count = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] segments = line.split(",");
            count++;
            Player player = new Player();
            player.setId(String.format("%s-%d", league, count));
            player.setName(segments[0]);
            player.setCountry(segments[1]);
            player.setTwitchName(Strings.isBlank(segments[2]) ? null : segments[2]);
            player.setTwitterName(Strings.isBlank(segments[3]) ? null : segments[3]);
            player.setStartPoints(Integer.parseInt(segments[4]));
            if (segments.length >= 8) {
                player.setDeckName(Strings.isBlank(segments[6]) ? null : segments[6]);
                player.setDeckLink(Strings.isBlank(segments[7]) ? null : segments[7]);
            }
            byNameMap.put(player.getName(), player);
        }

        scanner = loadFile(String.format("/mpl/%s.static.players", league));
        if (scanner != null) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] segments = line.split(",");
                String name = segments[0];
                Player player = byNameMap.get(name);
                if (player.getTwitchName() == null) {
                    player.setTwitchName(Strings.isBlank(segments[2]) ? null : segments[2]);
                }
                if (player.getTwitterName() == null) {
                    player.setTwitterName(Strings.isBlank(segments[3]) ? null : segments[3]);
                }
                player.setStartPoints(Integer.parseInt(segments[4]));
                if (segments.length >= 8) {
                    if (!Strings.isBlank(segments[6])) {
                        player.setDeckName(segments[6]);
                    }
                    if (!Strings.isBlank(segments[7])) {
                        player.setDeckLink(segments[7]);
                    }
                }
                byNameMap.put(player.getName(), player);
            }
        }

        Map<String, String> aliasMap = new HashMap<>();
        scanner = loadFile(String.format("/mpl/%s.alias", league));
        if (scanner != null) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] segments = line.split(",");
                aliasMap.put(segments[1], segments[0]);
            }
        }

        return new PlayerSet(byNameMap, aliasMap);
    }

    private Map<Integer, Round> loadRounds(final String league, final PlayerSet playerSet) {
        Scanner scanner =new Scanner(LeagueManager.class.getResourceAsStream(String.format("/mpl/%s.schedule", league)),
                StandardCharsets.UTF_8.name());

        Map<Integer, Round> roundMap = new HashMap<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] segments = line.split(",");
            int roundNumber = Integer.parseInt(segments[0]);

            Match match = new Match(roundNumber);
            Player player1 = playerSet.getByName(segments[1]);
            match.setPlayer1(player1);
            Player player2 = playerSet.getByName(segments[4]);
            match.setPlayer2(player2);
            if (player1 == null) {
                throw new IllegalStateException("Bad player: " + segments[1] + ": " + line);
            }
            if (player2 == null) {
                throw new IllegalStateException("Bad player: " + segments[4] + ": " + line);
            }

            if (!roundMap.containsKey(roundNumber)) {
                Round round = new Round(roundNumber);
                roundMap.put(roundNumber, round);
            }

            roundMap.get(roundNumber).getMatches().add(match);
        }

        return roundMap;
    }

    private Scanner loadFile(final String fileName) {
        InputStream inputStream = LeagueManager.class.getResourceAsStream(fileName);
        if (inputStream != null) {
            return new Scanner(inputStream, StandardCharsets.UTF_8.name());
        }
        return null;
    }

    private List<Round> roundize(final Map<Integer, Round> roundMap) {
        List<Round> rounds = new ArrayList<>();
        List<Integer> roundNumbers = new ArrayList<>(roundMap.keySet());
        Collections.sort(roundNumbers);
        roundNumbers.forEach(number -> rounds.add(roundMap.get(number)));
        return rounds;
    }

    private List<Schedule> makeSchedules(final PlayerSet players, final Map<Integer, Round> rounds) {
        Map<Player, Schedule> scheduleMap = new HashMap<>();
        for (Player player : players.getPlayers()) {
            scheduleMap.put(player, new Schedule(player));
        }

        for (Round round : rounds.values()) {
            for(Match match : round.getMatches()) {
                scheduleMap.get(match.getPlayer1()).getMatches().add(match);
                scheduleMap.get(match.getPlayer2()).getMatches().add(match);
            }
        }

        List<Schedule> schedules = new ArrayList<>();
        for (Schedule schedule : scheduleMap.values()) {
            Collections.sort(schedule.getMatches());
            schedules.add(schedule);
        }

        return schedules;
    }
}