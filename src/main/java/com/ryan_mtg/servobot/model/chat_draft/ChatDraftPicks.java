package com.ryan_mtg.servobot.model.chat_draft;

import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ChatDraftPicks {
    @Getter
    private final List<ChatDraftPack> packs = new ArrayList<>();

    public void addPack(final ChatDraftPack pack) {
        packs.add(pack);
    }

    public HomedUser getPicker(final int pack, final int pick) {
        if (1 <= pack && pack <= packs.size()) {
            return packs.get(pack - 1).getPicker(pick);
        }
        return null;
    }
}
