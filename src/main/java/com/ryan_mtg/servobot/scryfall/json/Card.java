package com.ryan_mtg.servobot.scryfall.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Card {
    private String name;

    @JsonProperty("mana_cost")
    private String manaCost;

    @JsonProperty("type_line")
    private String typeLine;

    @JsonProperty("oracle_text")
    private String oracleText;

    @JsonProperty("image_uris")
    private ImageUris imageUris;

    @JsonProperty("card_faces")
    private List<CardFace> cardFaces;
}
