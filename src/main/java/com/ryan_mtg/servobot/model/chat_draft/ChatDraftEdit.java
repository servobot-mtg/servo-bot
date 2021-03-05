package com.ryan_mtg.servobot.model.chat_draft;

import com.ryan_mtg.servobot.commands.CommandTableEdit;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ChatDraftEdit {
    private CommandTableEdit commandTableEdit = new CommandTableEdit();
    private Map<ChatDraft, Integer> savedChatDrafts = new HashMap<>();

    public void saveChatDraft(final int botHomeId, final ChatDraft chatDraft) {
        savedChatDrafts.put(chatDraft, botHomeId);
    }

    public void merge(final ChatDraftEdit chatDraftEdit) {
        commandTableEdit.merge(chatDraftEdit.commandTableEdit);
        savedChatDrafts.putAll(chatDraftEdit.savedChatDrafts);
    }

    public void merge(final CommandTableEdit commandTableEdit) {
        this.commandTableEdit.merge(commandTableEdit);
    }
}
