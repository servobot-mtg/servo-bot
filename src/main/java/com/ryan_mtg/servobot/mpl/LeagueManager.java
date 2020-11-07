package com.ryan_mtg.servobot.mpl;

import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class LeagueManager {
    @Getter
    private PlayerSet mpl;

    @Getter
    private PlayerSet rivals;

    public LeagueManager() {
        mpl = loadPlayers("mpl");
        rivals = loadPlayers("rivals");
    }

    private PlayerSet loadPlayers(final String league) {
        Scanner scanner = new Scanner(LeagueManager.class.getResourceAsStream(String.format("/mpl/%s.players", league)),
            StandardCharsets.UTF_8.name());

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

        InputStream staticFile = LeagueManager.class.getResourceAsStream(String.format("/mpl/%s.static.players", league));
        if (staticFile != null) {
            scanner = new Scanner(staticFile, StandardCharsets.UTF_8.name());

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] segments = line.split(",");
                String name = segments[0];
                Player player = byNameMap.get(name);
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

        return new PlayerSet(byNameMap);
    }
}