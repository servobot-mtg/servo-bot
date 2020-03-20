package com.ryan_mtg.servobot.model.reaction;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import lombok.Getter;

public class ReactionCommand {
    public static final int UNREGISTERED_ID = 0;

    private int id;

    @Getter
    private Command command;

    public ReactionCommand(final int id, final Command command) {
        this.id = id;
        this.command = command;
    }
}
