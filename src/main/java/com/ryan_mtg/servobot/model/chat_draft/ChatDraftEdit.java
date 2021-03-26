package com.ryan_mtg.servobot.model.chat_draft;

import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Getter
public class ChatDraftEdit {
    private CommandTableEdit commandTableEdit = new CommandTableEdit();
    private Map<ChatDraft, Integer> savedChatDrafts = new HashMap<>();
    private Map<ChatDraft, Function<ChatDraft, Command>> chatDraftSaveCallbackMap = new HashMap<>();
    private Map<DraftEntrant, Integer> savedDraftEntrants = new HashMap<>();
    private Set<DraftEntrant> deletedDraftEntrants = new HashSet<>();
    private Map<ChatDraftPick, Integer> savedChatDraftPicks = new HashMap<>();
    private Set<ChatDraftPick> deletedChatDraftPicks = new HashSet<>();

    public void saveChatDraft(final int botHomeId, final ChatDraft chatDraft) {
        savedChatDrafts.put(chatDraft, botHomeId);
    }

    public void saveChatDraft(final int botHomeId, final ChatDraft chatDraft,
            final Function<ChatDraft, Command> chatDraftSaveCallback) {
        saveChatDraft(botHomeId, chatDraft);
        chatDraftSaveCallbackMap.put(chatDraft, chatDraftSaveCallback);
    }

    public void saveDraftEntrant(final int chatDraftId, final DraftEntrant draftEntrant) {
        savedDraftEntrants.put(draftEntrant, chatDraftId);
    }

    public void deleteEntrants(final List<DraftEntrant> entrants) {
        deletedDraftEntrants.addAll(entrants);
    }

    public void saveChatDraftPick(final int chatDraftId, final ChatDraftPick chatDraftPick) {
        savedChatDraftPicks.put(chatDraftPick, chatDraftId);
    }

    public void deleteChatDraftPicks(final List<ChatDraftPick> chatDraftPicks) {
        deletedChatDraftPicks.addAll(chatDraftPicks);
    }

    public void merge(final ChatDraftEdit chatDraftEdit) {
        commandTableEdit.merge(chatDraftEdit.commandTableEdit);
        savedChatDrafts.putAll(chatDraftEdit.savedChatDrafts);
        chatDraftSaveCallbackMap.putAll(chatDraftEdit.chatDraftSaveCallbackMap);
        savedDraftEntrants.putAll(chatDraftEdit.savedDraftEntrants);
        deletedDraftEntrants.addAll(chatDraftEdit.deletedDraftEntrants);
        savedChatDraftPicks.putAll(chatDraftEdit.savedChatDraftPicks);
        deletedChatDraftPicks.addAll(chatDraftEdit.deletedChatDraftPicks);
    }

    public void merge(final CommandTableEdit commandTableEdit) {
        this.commandTableEdit.merge(commandTableEdit);
    }
}