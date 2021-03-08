package com.ryan_mtg.servobot.model.chat_draft;

import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ChatDraftPack {
    @Getter
    private final List<ChatDraftPick> picks = new ArrayList<>();

    public void addPick(final ChatDraftPick pick) {
        picks.add(pick);
    }

    public String getPackString() {
        StringBuilder message = new StringBuilder();
        message.append("Pack ").append(picks.get(0).getPack()).append(':');
        for (ChatDraftPick pick : picks) {
            message.append(' ').append(pick.getPicker().getName());
        }
        return message.toString();
    }

    public HomedUser getPicker(final int pick) {
        if (1 <= pick && pick <= picks.size()) {
            return picks.get(pick - 1).getPicker();
        }
        return null;
    }
}
