package com.ryan_mtg.servobot.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class PlayerDecklist {
    private Player player;
    private DecklistDescription decklistDescription;
}
