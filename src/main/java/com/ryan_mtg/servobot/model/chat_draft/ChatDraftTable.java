package com.ryan_mtg.servobot.model.chat_draft;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ChatDraftTable {
    @Getter
    private List<ChatDraft> chatDrafts = new ArrayList<>();

    public void add(final ChatDraft chatDraft) {
        chatDrafts.add(chatDraft);
    }

    public ChatDraftEdit addChatDraft(final int botHomeId, final ChatDraft chatDraft) {
        ChatDraftEdit chatDraftEdit = new ChatDraftEdit();
        add(chatDraft);
        chatDraftEdit.saveChatDraft(botHomeId, chatDraft);
        return chatDraftEdit;
    }
}
