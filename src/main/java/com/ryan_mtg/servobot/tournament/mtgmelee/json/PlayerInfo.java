package com.ryan_mtg.servobot.tournament.mtgmelee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PlayerInfo {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Username")
    private String username;

    @JsonProperty("Points")
    private int points;

    @JsonProperty("Rank")
    private int rank;

    @JsonProperty("Decklists")
    private List<DecklistJson> decklists;

    @JsonProperty("TwitchChannel")
    private String twitchChannel;

    @JsonProperty("FacebookPage")
    private String facebookPage;

    @JsonProperty("YouTubeChannel")
    private String youTubeChannel;

    @JsonProperty("TwitterHandle")
    private String twitterHandle;

    @JsonProperty("MatchWins")
    private int wins;

    @JsonProperty("MatchLoses")
    private int losses;

    @JsonProperty("MatchDraws")
    private int draws;
}
