package com.ryan_mtg.servobot.tournament.mtgmelee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DecklistJson {
    @JsonProperty("ID")
    private int id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Format")
    private String format;
}
