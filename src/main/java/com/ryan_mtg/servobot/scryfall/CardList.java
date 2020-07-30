package com.ryan_mtg.servobot.scryfall;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ryan_mtg.servobot.scryfall.json.Card;
import lombok.Data;

import java.util.List;

@Data
public class CardList {
    private String object;

    @JsonProperty("total_cards")
    private int totalCards;

    @JsonProperty("has_more")
    private boolean hasMore;

    @JsonProperty("next_page")
    private String nextPage;

    private List<Card> data;
}
