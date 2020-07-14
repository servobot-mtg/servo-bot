package com.ryan_mtg.servobot.tournament.mtgmelee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PairingInfo {
    @JsonProperty("TournamentId")
    int tournamentId;

    @JsonProperty("PhaseId")
    int phaseId;

    @JsonProperty("Player1")
    String player1Name;

    @JsonProperty("Player1DecklistId")
    int player1DecklistId;

    @JsonProperty("Player1Discord")
    String player1Discord;

    @JsonProperty("Player1ScreenName")
    String player1ArenaName;

    @JsonProperty("Player1Twitch")
    String player1Twitch;

    @JsonProperty("Player2")
    String player2Name;

    @JsonProperty("Player2DecklistId")
    int player2DecklistId;

    @JsonProperty("Player2Discord")
    String player2Discord;

    @JsonProperty("Player2ScreenName")
    String player2ArenaName;

    @JsonProperty("Player2Twitch")
    String player2Twitch;

    @JsonProperty("RoundNumber")
    int round;

    @JsonProperty("HasResults")
    boolean hasResults;

    @JsonProperty("Result")
    String result;
}