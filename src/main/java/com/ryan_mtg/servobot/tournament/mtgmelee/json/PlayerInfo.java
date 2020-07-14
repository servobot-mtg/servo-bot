package com.ryan_mtg.servobot.tournament.mtgmelee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PlayerInfo {
    @JsonProperty("Name")
    String name;

    @JsonProperty("Username")
    String username;

    @JsonProperty("Points")
    int points;

    @JsonProperty("Rank")
    int rank;

    @JsonProperty("DecklistId")
    int decklistId;

    @JsonProperty("DecklistName")
    String decklistName;

    @JsonProperty("TwitchChannel")
    String twitchChannel;
}
