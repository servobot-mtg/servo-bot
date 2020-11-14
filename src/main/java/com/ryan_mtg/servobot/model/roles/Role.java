package com.ryan_mtg.servobot.model.roles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Role {
    public static final int UNREGISTERED_ID = 0;

    @Getter @Setter
    private int id;

    @Getter
    private String role;

    @Getter
    private String emote;

    @Getter @Setter
    private boolean appendEmote;
}
