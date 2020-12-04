package com.ryan_mtg.servobot.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DecklistMap {
    private Map<DecklistKey, DecklistDescription> map = new HashMap<>();

    public void put(final Player player, final String format, final DecklistDescription decklistDescription) {
       map.put(new DecklistKey(player, format), decklistDescription);
    }

    public DecklistDescription get(final Player player, final String format) {
        return map.get(new DecklistKey(player, format));
    }

    public Set<PlayerDecklist> getDecklists(final String format) {
        Set<PlayerDecklist> decklists = new HashSet<>();
        for (Map.Entry<DecklistKey, DecklistDescription> entry : map.entrySet()) {
            if (entry.getKey().getFormat().equals(format)) {
                decklists.add(new PlayerDecklist(entry.getKey().getPlayer(), entry.getValue()));
            }
        }
        return decklists;
    }

    @Data @AllArgsConstructor
    private static class DecklistKey {
        private Player player;
        private String format;
    }
}