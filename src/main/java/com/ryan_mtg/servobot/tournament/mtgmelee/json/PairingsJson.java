package com.ryan_mtg.servobot.tournament.mtgmelee.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PairingsJson {
    int draw;
    int recordsTotal;
    int recordsFiltered;
    List<PairingInfo> data;
}
