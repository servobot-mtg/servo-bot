package com.ryan_mtg.servobot.mpl;

import lombok.Data;

@Data
public class Player {
    private String id;
    private String name;
    private String country;
    private int startPoints;
    private String twitchName;
    private String twitterName;
    private String picture;
    private String deckName;
    private String deckLink;
}