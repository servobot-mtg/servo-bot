package com.ryan_mtg.servobot.scryfall.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardFace {
    @JsonProperty("image_uris")
    private ImageUris imageUris;

    private String name;
}
