package com.ryan_mtg.servobot.game.sus;

import com.ryan_mtg.servobot.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class KillAction implements SusAction {
    private User killer;
    private User victim;

    @Override
    public User getActor() {
        return killer;
    }
}
