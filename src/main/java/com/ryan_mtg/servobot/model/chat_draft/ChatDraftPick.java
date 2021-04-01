package com.ryan_mtg.servobot.model.chat_draft;

import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;
import lombok.Setter;

public class ChatDraftPick {
    public static final int UNREGISTERED_ID = 0;

    @Getter @Setter
    private int id;

    @Getter
    private final int pack;

    @Getter
    private final int pick;

    @Getter
    private final HomedUser picker;

    public ChatDraftPick(final int id, final int pack, final int pick, final HomedUser picker) {
        this.id =  id;
        this.pack = pack;
        this.pick = pick;
        this.picker = picker;
    }
}
