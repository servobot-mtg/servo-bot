package com.ryan_mtg.servobot.model.editors;

import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.chat_draft.BeginChatDraftCommand;
import com.ryan_mtg.servobot.commands.chat_draft.ChatDraftStatusCommand;
import com.ryan_mtg.servobot.commands.chat_draft.CloseChatDraftCommand;
import com.ryan_mtg.servobot.commands.chat_draft.EnterChatDraftCommand;
import com.ryan_mtg.servobot.commands.chat_draft.NextPickCommand;
import com.ryan_mtg.servobot.commands.chat_draft.OpenChatDraftCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.data.factories.ChatDraftSerializer;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraft;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftEdit;
import com.ryan_mtg.servobot.model.chat_draft.ChatDraftTable;
import com.ryan_mtg.servobot.model.chat_draft.DraftEntrant;
import com.ryan_mtg.servobot.model.giveaway.GiveawayCommandSettings;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.HomedUserTable;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ChatDraftEditor {
    private final int contextId;
    private final ChatDraftTable chatDraftTable;
    private final ChatDraftSerializer chatDraftSerializer;
    private final CommandTableEditor commandTableEditor;
    private final HomedUserTable userTable;

    public ChatDraft getChatDraft(final int chatDraftId) {
        return chatDraftTable.getChatDraft(chatDraftId);
    }

    public ChatDraft addChatDraft() throws UserError {
        ChatDraft chatDraft = new ChatDraft();

        String openCommandName = chatDraft.getOpenCommandSettings().getCommandName();
        validateCommand(openCommandName);

        GiveawayCommandSettings openSettings = chatDraft.getOpenCommandSettings();
        OpenChatDraftCommand openCommand = new OpenChatDraftCommand(Command.UNREGISTERED_ID,
                createCommandSettings(openSettings), ChatDraft.UNREGISTERED_ID, openSettings.getMessage());
        chatDraft.setOpenCommand(openCommand);

        ChatDraftEdit chatDraftEdit = chatDraftTable.addChatDraft(contextId, chatDraft, (savedChatDraft) -> {
            openCommand.setChatDraftId(savedChatDraft.getId());
            return openCommand;
        });
        chatDraftEdit.merge(commandTableEditor.addCommandEdit(openCommandName, openCommand));

        chatDraftSerializer.commit(chatDraftEdit);
        return chatDraft;
    }

    public ChatDraft openChatDraft(final int chatDraftId) throws UserError {
        ChatDraft chatDraft = getChatDraft(chatDraftId);

        switch (chatDraft.getState()) {
            case RECRUITING:
            case ACTIVE:
                throw new UserError("A chat draft is already going on.");
        }

        GiveawayCommandSettings enterSettings = chatDraft.getEnterCommandSettings();
        String enterCommandName = enterSettings.getCommandName();
        validateCommand(enterCommandName);

        GiveawayCommandSettings beginSettings = chatDraft.getBeginCommandSettings();
        String beginCommandName = beginSettings.getCommandName();
        validateCommand(beginCommandName);

        EnterChatDraftCommand enterCommand = new EnterChatDraftCommand(Command.UNREGISTERED_ID,
                createCommandSettings(enterSettings), chatDraft.getId(), enterSettings.getMessage());
        chatDraft.setEnterCommand(enterCommand);
        CommandTableEdit commandTableEdit = commandTableEditor.addCommandEdit(enterCommandName, enterCommand);

        GiveawayCommandSettings statusSettings = chatDraft.getStatusCommandSettings();
        String statusCommandName = statusSettings.getCommandName();
        if (!commandTableEditor.hasCommand(statusCommandName)) {
            ChatDraftStatusCommand statusCommand = new ChatDraftStatusCommand(Command.UNREGISTERED_ID,
                    createCommandSettings(statusSettings), chatDraft.getId(), statusSettings.getMessage());
            chatDraft.setStatusCommand(statusCommand);
            commandTableEdit.merge(commandTableEditor.addCommandEdit(statusCommandName, statusCommand));
        }

        BeginChatDraftCommand beginCommand = new BeginChatDraftCommand(Command.UNREGISTERED_ID,
                createCommandSettings(beginSettings), chatDraft.getId(), beginSettings.getMessage());
        chatDraft.setBeginCommand(beginCommand);
        commandTableEdit.merge(commandTableEditor.addCommandEdit(beginCommandName, beginCommand));

        chatDraft.setState(ChatDraft.State.RECRUITING);

        ChatDraftEdit chatDraftEdit = new ChatDraftEdit();
        chatDraftEdit.saveChatDraft(contextId, chatDraft);
        chatDraftEdit.merge(commandTableEdit);

        List<DraftEntrant> entrants = chatDraft.getEntrants();
        if (!entrants.isEmpty()) {
            chatDraftEdit.deleteEntrants(entrants);
            entrants.clear();
        }

        chatDraftEdit.merge(chatDraft.deletePicks());

        chatDraftSerializer.commit(chatDraftEdit);
        return chatDraft;
    }

    public ChatDraft beginChatDraft(final int chatDraftId) throws UserError {
        ChatDraft chatDraft = getChatDraft(chatDraftId);

        GiveawayCommandSettings nextSettings = chatDraft.getNextCommandSettings();
        String nextCommandName = nextSettings.getCommandName();
        validateCommand(nextCommandName);

        GiveawayCommandSettings closeSettings = chatDraft.getCloseCommandSettings();
        String closeCommandName = closeSettings.getCommandName();
        validateCommand(closeCommandName);

        ChatDraftEdit chatDraftEdit = chatDraft.beginDraft(contextId);

        NextPickCommand nextCommand = new NextPickCommand(Command.UNREGISTERED_ID, createCommandSettings(nextSettings),
                chatDraft.getId(), nextSettings.getMessage());
        chatDraft.setNextCommand(nextCommand);
        CommandTableEdit commandTableEdit = commandTableEditor.addCommandEdit(nextCommandName, nextCommand);

        CloseChatDraftCommand closeCommand = new CloseChatDraftCommand(Command.UNREGISTERED_ID,
                createCommandSettings(closeSettings), chatDraft.getId(), closeSettings.getMessage());
        chatDraft.setCloseCommand(closeCommand);
        commandTableEdit.merge(commandTableEditor.addCommandEdit(closeCommandName, closeCommand));

        SystemError.filter(() -> {
            commandTableEditor.deleteCommand(chatDraft.getEnterCommand().getId(), commandTableEdit);
            chatDraft.setEnterCommand(null);

            commandTableEditor.deleteCommand(chatDraft.getBeginCommand().getId(), commandTableEdit);
            chatDraft.setBeginCommand(null);
        });

        chatDraftEdit.merge(commandTableEdit);
        chatDraftSerializer.commit(chatDraftEdit);
        return chatDraft;
    }

    public ChatDraft closeChatDraft(final int chatDraftId) throws UserError {
        ChatDraft chatDraft = getChatDraft(chatDraftId);

        CommandTableEdit commandTableEdit = new CommandTableEdit();
        SystemError.filter(() -> {
            commandTableEditor.deleteCommand(chatDraft.getStatusCommand().getId(), commandTableEdit);
            chatDraft.setStatusCommand(null);

            commandTableEditor.deleteCommand(chatDraft.getNextCommand().getId(), commandTableEdit);
            chatDraft.setNextCommand(null);

            commandTableEditor.deleteCommand(chatDraft.getCloseCommand().getId(), commandTableEdit);
            chatDraft.setCloseCommand(null);
        });

        chatDraft.setState(ChatDraft.State.CONFIGURING);

        ChatDraftEdit chatDraftEdit = new ChatDraftEdit();
        chatDraftEdit.saveChatDraft(contextId, chatDraft);
        chatDraftEdit.merge(commandTableEdit);

        List<DraftEntrant> entrants = chatDraft.getEntrants();
        if (!entrants.isEmpty()) {
            chatDraftEdit.deleteEntrants(entrants);
            entrants.clear();
        }

        chatDraftEdit.merge(chatDraft.deletePicks());
        chatDraftSerializer.commit(chatDraftEdit);
        return chatDraft;
    }

    public ChatDraft enterChatDraft(final int chatDraftId, final HomedUser entrant) throws UserError {
        ChatDraft chatDraft = getChatDraft(chatDraftId);

        DraftEntrant draftEntrant = new DraftEntrant(DraftEntrant.UNREGISTERED_ID, entrant);
        ChatDraftEdit chatDraftEdit = chatDraft.addDraftEntrant(draftEntrant);

        chatDraftSerializer.commit(chatDraftEdit);
        return chatDraft;
    }

    public ChatDraft nextPick(final int chatDraftId) {
        ChatDraft chatDraft = getChatDraft(chatDraftId);
        ChatDraftEdit chatDraftEdit = chatDraft.nextPick(contextId);
        chatDraftSerializer.commit(chatDraftEdit);
        return chatDraft;
    }

    private void validateCommand(final String commandName) throws UserError {
        if (commandTableEditor.hasCommand(commandName)) {
            throw new UserError("There is already a '%s' command.", commandName);
        }
    }

    private CommandSettings createCommandSettings(final GiveawayCommandSettings commandSettings) {
        return new CommandSettings(commandSettings.getFlags(), commandSettings.getPermission(), new RateLimit());
    }
}
