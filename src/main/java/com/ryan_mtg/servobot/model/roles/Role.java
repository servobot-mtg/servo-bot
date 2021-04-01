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
    private final String role;

    @Getter
    private final long roleId;

    @Getter
    private final String emote;

    @Getter @Setter
    private boolean appendEmote;
}
