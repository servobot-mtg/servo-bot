package com.ryan_mtg.servobot.scryfall;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Card {
    private String name;

    @JsonProperty("mana_cost")
    private String manaCost;

    @JsonProperty("type_line")
    private String typeLine;

    @JsonProperty("oracle_text")
    private String oracleText;
}
