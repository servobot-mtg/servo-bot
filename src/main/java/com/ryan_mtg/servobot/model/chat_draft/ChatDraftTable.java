package com.ryan_mtg.servobot.model.chat_draft;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.error.SystemError;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ChatDraftTable {
    @Getter
    private List<ChatDraft> chatDrafts = new ArrayList<>();

    public void add(final ChatDraft chatDraft) {
        chatDrafts.add(chatDraft);
    }

    public ChatDraftEdit addChatDraft(final int botHomeId, final ChatDraft chatDraft,
            final Function<ChatDraft, Command> chatDraftSaveCallback) {
        ChatDraftEdit chatDraftEdit = new ChatDraftEdit();
        add(chatDraft);
        chatDraftEdit.saveChatDraft(botHomeId, chatDraft, chatDraftSaveCallback);
        return chatDraftEdit;
    }

    public ChatDraft getChatDraft(final int chatDraftId) {
        return chatDrafts.stream().filter(chatDraft -> chatDraft.getId() == chatDraftId).findFirst()
                .orElseThrow(() -> new SystemError("Chat draft with id %d not found.", chatDraftId));
    }
}