package com.ryan_mtg.servobot.scryfall;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private int status;
    private String code;
    private String details;
    private String type;
    private List<String> warnings;
}
